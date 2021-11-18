package main.service;

import main.dto.PostDTO;
import main.mapper.PostMapper;
import main.model.ModerationStatus;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final int POSTS_ON_PAGE = 10;
    private final PostsRepository postsRepository;
    private final PostMapper mapper;

    @Autowired
    public PostServiceImpl(PostsRepository postsRepository, PostMapper mapper) {
        this.postsRepository = postsRepository;
        this.mapper = mapper;
    }

    @Override
    public List<PostDTO> getPosts(int offset, int limit, String mode) {
        List<PostDTO> postDTOs = new ArrayList<>();
        int pageNumber = offset / POSTS_ON_PAGE;
        switch (mode) {
            case "recent": {
                Sort dateSort = Sort.by(Sort.Direction.DESC, "time");
                Pageable page = PageRequest.of(pageNumber, limit, dateSort);
                postDTOs = postsRepository.findAllByIsActiveAndModerationStatus((byte)1, ModerationStatus.ACCEPTED, page)
                        .stream().map(mapper::toDTO).collect(Collectors.toList());
                break;
            }
            case "popular": {
                Pageable page = PageRequest.of(pageNumber, limit);
                postDTOs = postsRepository.findAllByPostCommentCount(page).stream().map(mapper::toDTO).collect(Collectors.toList());
                break;
            }
            case "best": {
                Pageable page = PageRequest.of(pageNumber, limit);
                postDTOs = postsRepository.findAllLikedPosts(page).stream().map((mapper::toDTO)).collect(Collectors.toList());
                break;
            }
            case "early": {
                Sort dateSort = Sort.by(Sort.Direction.ASC, "time");
                Pageable page = PageRequest.of(pageNumber, limit, dateSort);
                postDTOs = postsRepository.findAllByIsActiveAndModerationStatus((byte)1, ModerationStatus.ACCEPTED, page)
                        .stream().map(mapper::toDTO).collect(Collectors.toList());
                break;
            }
        }
        return postDTOs;
    }

    public long postCount() {
        return postsRepository.countPostsByIsActiveAndModerationStatus((byte)1, ModerationStatus.ACCEPTED);
    }
}
