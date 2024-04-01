package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.dto.PostDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class PostsResponse {
    private long count;
    private List<PostDTO> posts;

    public PostsResponse() {
        this.posts = new ArrayList<>();
    }
}
