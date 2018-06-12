package skills.com.sem.skillsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SkillActivity extends AppCompatActivity {

    private Toolbar skillToolbar;
    private RecyclerView skill_list_view;
    private FloatingActionButton addPostBtn;

    private SkillRecyclerAdapter skillRecyclerAdapter;
    public List<SkillPost> skill_list;
    public List<User> user_list;
    public List<Category> category_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String category_id;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);

        skillToolbar = findViewById(R.id.skills_toolbar);
        setSupportActionBar(skillToolbar);

        skill_list = new ArrayList<>();
        user_list = new ArrayList<>();
        category_list = new ArrayList<>();
        skill_list_view = findViewById(R.id.skill_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        category_id = getIntent().getStringExtra("category_id");

        skillRecyclerAdapter = new SkillRecyclerAdapter(skill_list, user_list, SkillActivity.this);
        skill_list_view.setLayoutManager(new LinearLayoutManager(SkillActivity.this));
        skill_list_view.setAdapter(skillRecyclerAdapter);
        skill_list_view.setHasFixedSize(true);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            skill_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(-1);

                    if (reachedBottom) {

                        String desc = lastVisible.getString("desc");

                        loadMorePosts();

                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("Category/" + category_id + "/Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(SkillActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            skill_list.clear();
                            user_list.clear();

                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String skillPostId = doc.getDocument().getId();
                                final SkillPost skillPost = doc.getDocument().toObject(SkillPost.class).withId(skillPostId);

                                String skillUserId = doc.getDocument().getString("user_id");
                                firebaseFirestore.collection("Users").document(skillUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            User user = task.getResult().toObject(User.class);

                                            if (isFirstPageFirstLoad) {

                                                user_list.add(user);
                                                skill_list.add(skillPost);

                                            } else {

                                                user_list.add(user);
                                                skill_list.add(0, skillPost);

                                            }

                                            skillRecyclerAdapter.notifyDataSetChanged();

                                        }

                                    }
                                });

                            }
                        }

                        isFirstPageFirstLoad = false;

                    }
                }
            });

            addPostBtn = findViewById(R.id.add_skill_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent newSkillIntent = new Intent(SkillActivity.this, NewSkillActivity.class);
                    newSkillIntent.putExtra("category_id", category_id);
                    startActivity(newSkillIntent);

                }
            });

        }

    }

    public String getId() {

        return category_id;

    }

    public void loadMorePosts() {

        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Category/" + category_id + "/Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(SkillActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String skillPostId = doc.getDocument().getId();
                                final SkillPost skillPost = doc.getDocument().toObject(SkillPost.class).withId(skillPostId);
                                String skillUserId = doc.getDocument().getString("user_id");

                                firebaseFirestore.collection("Users").document(skillUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            User user = task.getResult().toObject(User.class);

                                            user_list.add(user);
                                            skill_list.add(skillPost);

                                            skillRecyclerAdapter.notifyDataSetChanged();

                                        }

                                    }
                                });

                            }
                        }
                    }

                }
            });

        }

    }
}
