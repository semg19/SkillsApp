package skills.com.sem.skillsapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SkillRecyclerAdapter extends RecyclerView.Adapter<SkillRecyclerAdapter.ViewHolder> {

    public List<SkillPost> skill_list;
    public List<User> user_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public SkillRecyclerAdapter(List<SkillPost> skill_list, List<User> user_list) {

        this.skill_list = skill_list;
        this.user_list = user_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String skillPostId = skill_list.get(position).SkillPostId;
        final String currentUserID = firebaseAuth.getCurrentUser().getUid();

        String desc_data = skill_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String skill_user_id = skill_list.get(position).getUser_id();

        if (skill_user_id.equals(currentUserID)) {

            holder.skillDeleteBtn.setEnabled(true);
            holder.skillDeleteBtn.setVisibility(View.VISIBLE);

        }

        String userName = user_list.get(position).getName();
        String userImage = user_list.get(position).getImage();

        holder.setUserData(userName, userImage);

        long milliseconds = skill_list.get(position).getTimestamp().getTime();
        String dateString = new SimpleDateFormat().format(new Date(milliseconds));
        holder.setTime(dateString);

        firebaseFirestore.collection("Posts/" + skillPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (firebaseAuth.getCurrentUser() != null) {

                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();

                        holder.updateLikesCount(count);

                    } else {

                        holder.updateLikesCount(0);

                    }

                }
            }
        });

        firebaseFirestore.collection("Posts/" + skillPostId + "/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (firebaseAuth.getCurrentUser() != null) {

                    if (documentSnapshot.exists()) {

                        holder.skillLikeButton.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                    } else {

                        holder.skillLikeButton.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));

                    }

                }
            }
        });

        // Likes
        holder.skillLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts/" + skillPostId + "/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()) {

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + skillPostId + "/Likes").document(currentUserID).set(likesMap);
                        } else {

                            firebaseFirestore.collection("Posts/" + skillPostId + "/Likes").document(currentUserID).delete();

                        }

                    }
                });

            }
        });

        holder.skillCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("skill_post_id", skillPostId);
                context.startActivity(commentIntent);

            }
        });

        holder.skillDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts").document(skillPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        skill_list.remove(position);
                        user_list.remove(position);

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return skill_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private TextView skillDate;

        private TextView skillUserName;
        private CircleImageView skillUserImage;

        private ImageView skillLikeButton;
        private TextView skillLikeCount;

        private ImageView skillCommentBtn;
        private Button skillDeleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            skillLikeButton = mView.findViewById(R.id.skill_like_btn);
            skillCommentBtn = mView.findViewById(R.id.skill_comment_btn);
            skillDeleteBtn = mView.findViewById(R.id.skill_delete_btn);
        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.skill_desc);
            descView.setText(descText);

        }

        public void setTime(String date) {

            skillDate = mView.findViewById(R.id.skill_date);
            skillDate.setText(date);

        }

        public void setUserData(String name, String image){

            skillUserImage = mView.findViewById(R.id.skill_user_image);
            skillUserName = mView.findViewById(R.id.skill_user_name);

            skillUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(skillUserImage);

        }

        public void updateLikesCount(int count) {

            skillLikeCount = mView.findViewById(R.id.skill_like_count);
            skillLikeCount.setText(count + " Likes");

        }

    }

}
