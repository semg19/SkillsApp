package skills.com.sem.skillsapp;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NewSkillActivity extends AppCompatActivity {

    private Toolbar newSkillToolbar;
    private ProgressBar newSkillProgress;

    private EditText newPostDesc;
    private Button newPostBtn;

    private Uri videoUri = null;
    private static final int REQUEST_CODE = 101;
//    private StorageReference videoRef;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;
    private String category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_skill);

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        videoRef = storageRef.child("/videos/" + uid + "/userIntro.3gp");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        category_id = getIntent().getStringExtra("category_id");
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newSkillToolbar = findViewById(R.id.new_skill_toolbar);
        setSupportActionBar(newSkillToolbar);
        getSupportActionBar().setTitle("Add New Skill");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostDesc = findViewById(R.id.new_skill_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newSkillProgress = findViewById(R.id.new_skill_progress);

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String desc = newPostDesc.getText().toString();

                if (!TextUtils.isEmpty(desc) && videoUri != null){

                    newSkillProgress.setVisibility(View.VISIBLE);

                    String randomName = FieldValue.serverTimestamp().toString();

                    StorageReference filePath = storageReference.child("post_movies").child(randomName + ".3gp");
                    filePath.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()){

                                String downloadUri = task.getResult().getDownloadUrl().toString();

                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("movie_url", downloadUri);
                                postMap.put("desc", desc);
                                postMap.put("user_id", current_user_id);
                                postMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Category/" + category_id + "/Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(NewSkillActivity.this, "Upload completed", Toast.LENGTH_LONG).show();
                                            Intent mainIntent = new Intent(NewSkillActivity.this, SkillActivity.class);
                                            startActivity(mainIntent);
                                            finish();

                                        } else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(NewSkillActivity.this, "Upload failed: " + error, Toast.LENGTH_LONG).show();

                                        }

                                        newSkillProgress.setVisibility(View.INVISIBLE);

                                    }
                                });

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(NewSkillActivity.this, "Upload failed: " + error, Toast.LENGTH_LONG).show();

                                newSkillProgress.setVisibility(View.INVISIBLE);

                            }

                        }
                    });

                } else if (TextUtils.isEmpty(desc) && videoUri == null) {

                    Toast.makeText(NewSkillActivity.this, "Please upload a complete skill.",
                    Toast.LENGTH_LONG).show();

                } else if (TextUtils.isEmpty(desc)) {

                    Toast.makeText(NewSkillActivity.this, "Please fill in a description.",
                    Toast.LENGTH_LONG).show();

                } else if (videoUri == null) {

                    Toast.makeText(NewSkillActivity.this, "Please upload a video.",
                    Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    public void record(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        videoUri = data.getData();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(NewSkillActivity.this, "Video saved to:\n" + videoUri, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(NewSkillActivity.this, "Video recording cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(NewSkillActivity.this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }
    }
}
