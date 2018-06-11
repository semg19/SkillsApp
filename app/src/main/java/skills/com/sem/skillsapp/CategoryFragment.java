package skills.com.sem.skillsapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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
public class CategoryFragment extends Fragment {

    private RecyclerView category_list_view;
    private List<Category> category_list;
    private List<User> user_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private CategoryRecyclerAdapter categoryRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        category_list = new ArrayList<>();
        user_list = new ArrayList<>();
        category_list_view = view.findViewById(R.id.category_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        categoryRecyclerAdapter = new CategoryRecyclerAdapter(category_list, user_list);
        category_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        category_list_view.setAdapter(categoryRecyclerAdapter);
        category_list_view.setHasFixedSize(true);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            category_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(-1);

                    if (reachedBottom) {

                        String name = lastVisible.getString("name");

                        loadMoreCategories();

                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("Category").orderBy("name", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            category_list.clear();

                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String categoryId = doc.getDocument().getId();
                                final Category category = doc.getDocument().toObject(Category.class).withId(categoryId);

                                category_list.add(category);

                                categoryRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                        isFirstPageFirstLoad = false;

                    }
                }
            });

        }

        // Inflate the layout for this fragment
        return view;
    }

    public void loadMoreCategories() {

        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Category")
                    .orderBy("name", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String categoryId = doc.getDocument().getId();
                                final Category category = doc.getDocument().toObject(Category.class).withId(categoryId);

                                category_list.add(category);

                                categoryRecyclerAdapter.notifyDataSetChanged();

                            }
                        }
                    }

                }
            });

        }

    }
}