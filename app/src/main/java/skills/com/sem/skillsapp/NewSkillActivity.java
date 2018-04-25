package skills.com.sem.skillsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class NewSkillActivity extends AppCompatActivity {

    private Toolbar newSkillToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_skill);

        newSkillToolbar = findViewById(R.id.new_skill_toolbar);
        setSupportActionBar(newSkillToolbar);
        getSupportActionBar().setTitle("Add New Skill");
    }
}
