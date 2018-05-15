package skills.com.sem.skillsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView skill_list_view;
    private List<SkillPost> skill_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private SkillRecyclerAdapter skillRecyclerAdapter;

    private DocumentSnapshot lastVisible;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        skill_list = new ArrayList<>();
        skill_list_view = view.findViewById(R.id.skill_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        skillRecyclerAdapter= new SkillRecyclerAdapter(skill_list);
        skill_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        skill_list_view.setAdapter(skillRecyclerAdapter);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            skill_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(-1);

                    if (reachedBottom) {

                        String desc = lastVisible.getString("desc");
                        Toast.makeText(container.getContext(), "Reached : " + desc, Toast.LENGTH_SHORT).show();

                        loadMorePosts();

                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            SkillPost skillPost = doc.getDocument().toObject(SkillPost.class);
                            skill_list.add(skillPost);

                            skillRecyclerAdapter.notifyDataSetChanged();

                        }
                    }

                }
            });

        }

        // Inflate the layout for this fragment
        return view;
    }

    public void loadMorePosts() {

        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            SkillPost skillPost = doc.getDocument().toObject(SkillPost.class);
                            skill_list.add(skillPost);

                            skillRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }

            }
        });

    }
}
