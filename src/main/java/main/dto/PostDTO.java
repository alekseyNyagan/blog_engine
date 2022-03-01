package main.dto;

import java.util.Objects;

public class PostDTO extends AbstractDTO {
    private int id;
    private long timestamp;
    private IncompleteUserDTO user;
    private String title;
    private String announce;
    private int likeCount;
    private int commentCount;
    private int dislikeCount;
    private int viewCount;

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

    public IncompleteUserDTO getUser() {
        return user;
    }

    public void setUser(IncompleteUserDTO user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostDTO postDTO = (PostDTO) o;
        return id == postDTO.id && timestamp == postDTO.timestamp && likeCount == postDTO.likeCount && commentCount == postDTO.commentCount && dislikeCount == postDTO.dislikeCount && viewCount == postDTO.viewCount && Objects.equals(user, postDTO.user) && Objects.equals(title, postDTO.title) && Objects.equals(announce, postDTO.announce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, user, title, announce, likeCount, commentCount, dislikeCount, viewCount);
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", announce='" + announce + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", dislikeCount=" + dislikeCount +
                ", viewCount=" + viewCount +
                '}';
    }
}
