package main.api.response;

import main.dto.PostDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostsResponse {
    private long count;
    private List<PostDTO> posts;

    public PostsResponse() {
        this.count = 0;
        this.posts = new ArrayList<>();
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostsResponse that = (PostsResponse) o;
        return count == that.count && Objects.equals(posts, that.posts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, posts);
    }

    @Override
    public String toString() {
        return "PostsResponse{" +
                "count=" + count +
                ", posts=" + posts +
                '}';
    }
}
