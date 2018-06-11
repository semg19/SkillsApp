package skills.com.sem.skillsapp;

public class Category extends CategoryId {

    public String name;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
