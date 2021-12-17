package main.dto;

public class PostCommentDTO extends AbstractDTO {
    private int id;
    private long timestamp;
    private String text;
    private IncompleteUserDTO user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public IncompleteUserDTO getUser() {
        return user;
    }

    public void setUser(IncompleteUserDTO user) {
        this.user = user;
    }
}
