package skills.com.sem.skillsapp;

import java.util.Date;

public class SkillPost extends SkillPostId {

    public String user_id, movie_url, desc;
    public Date timestamp;

    public SkillPost() {}

    public SkillPost(String user_id, String movie_url, String desc, Date timestamp) {
        this.user_id = user_id;
        this.movie_url = movie_url;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMovie_url() {
        return movie_url;
    }

    public void setMovie_url(String movie_url) {
        this.movie_url = movie_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
