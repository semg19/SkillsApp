package skills.com.sem.skillsapp;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class SkillPostId {

    @Exclude
    public String SkillPostId;

    public <T extends SkillPostId> T withId(@NonNull final String id) {
        this.SkillPostId = id;
        return (T) this;
    }
}
