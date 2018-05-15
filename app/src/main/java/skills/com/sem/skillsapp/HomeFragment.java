package skills.com.sem.skillsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private SkillRecyclerAdapter skillRecyclerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        skill_list = new ArrayList<>();
        skill_list_view = view.findViewById(R.id.skill_list_view);

        skillRecyclerAdapter= new SkillRecyclerAdapter(skill_list);
        skill_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        skill_list_view.setAdapter(skillRecyclerAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc: documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                     SkillPost skillPost = doc.getDocument().toObject(SkillPost.class);
                     skill_list.add(skillPost);

                     skillRecyclerAdapter.notifyDataSetChanged();

                    }
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
