package us.redsols.todo.model;

public class EditUser {

    String email;
    String timezone;
    String id;

    public EditUser(
            String email,
            String timezone,
            String id) {
        this.email = email;
        this.timezone = timezone;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getUid() {
        return id;
    }

    public void setUid(String id) {
        this.id = id;
    }
}
