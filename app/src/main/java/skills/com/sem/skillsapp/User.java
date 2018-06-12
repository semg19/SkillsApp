package skills.com.sem.skillsapp;

public class User {

    public String image, name, role;

    public User() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public User(String image, String name, String role) {
        this.image = image;
        this.name = name;
        this.role = role;
    }
}
