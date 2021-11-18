package main.api.response;

import main.dto.PostDTO;

import java.util.ArrayList;
import java.util.List;

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
}
