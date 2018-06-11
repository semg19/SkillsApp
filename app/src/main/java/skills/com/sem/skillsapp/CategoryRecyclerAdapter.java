package skills.com.sem.skillsapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {

    public List<Category> category_list;
    public List<User> user_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public CategoryRecyclerAdapter(List<Category> category_list, List<User> user_list) {

        this.category_list = category_list;
        this.user_list = user_list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String categoryId = category_list.get(position).CategoryId;
        final String currentUserID = firebaseAuth.getCurrentUser().getUid();

        String name_data = category_list.get(position).getName();
        holder.setName(name_data);

        holder.categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent skillIntent = new Intent(context, SkillActivity.class);
            skillIntent.putExtra("category_id", categoryId);
            context.startActivity(skillIntent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return category_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView category_name;
        private Button categoryBtn;

        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            categoryBtn = mView.findViewById(R.id.category_btn);

        }

        public void setName(String name){

            category_name = mView.findViewById(R.id.category_name);
            category_name.setText(name);

        }

    }

}