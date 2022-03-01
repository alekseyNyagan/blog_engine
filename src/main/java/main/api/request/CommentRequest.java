package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentRequest {
    @JsonProperty("parent_id")
    private Object parentId;
    @JsonProperty("post_id")
    private int postId;
    private String text;

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
