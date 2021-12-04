package pl.gorniak.audiogram2_0;

public class User {

    private String fullname;
    private String phoneNumber;
    private String location;
    private String email;

    public User(){

    }

    public User(String fullname, String phoneNumber, String location, String email) {
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
