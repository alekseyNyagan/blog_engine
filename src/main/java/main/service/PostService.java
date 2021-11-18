package main.service;

import main.dto.PostDTO;

import java.util.List;

public interface PostService {
    public List<PostDTO> getPosts(int offset, int limit, String mode);
    public long postCount();
}
