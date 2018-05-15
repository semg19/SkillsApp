package skills.com.sem.skillsapp;

public class SkillPost {

    public String user_id, movie_url, desc;

    public SkillPost() {}

    public SkillPost(String user_id, String movie_url, String desc) {
        this.user_id = user_id;
        this.movie_url = movie_url;
        this.desc = desc;
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
}
