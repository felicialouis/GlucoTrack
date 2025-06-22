package edu.uph.m23si3.glucotrack.Model;

public class User extends Account {
    private String name;
    private int age;
    private String gender;

    public User(String email, String password, String name, int age, String gender) {
        super(email, password); // memanggil constructor dari Account
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    // Getter & Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
