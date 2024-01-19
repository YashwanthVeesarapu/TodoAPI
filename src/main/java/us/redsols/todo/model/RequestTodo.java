package us.redsols.todo.model;

public class RequestTodo {

    private String title;
    private String date;
    private boolean completed;
    private boolean repeat;
    private boolean remind;
    private String uid;
    private String username;

    private String token;

    public RequestTodo(String title, String date, boolean completed, boolean repeat, boolean remind, String uid, String username, String token) {
        this.title = title;
        this.date = date;
        this.completed = completed;
        this.repeat = repeat;
        this.remind = remind;
        this.uid = uid;
        this.username = username;
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isRemind() {
        return remind;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
