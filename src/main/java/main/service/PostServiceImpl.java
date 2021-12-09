package main.service;

import main.api.response.CalendarResponse;
import main.api.response.PostsResponse;
import main.dto.CalendarDTO;
import main.dto.PostDTO;
import main.exceptions.NoSuchPostException;
import main.mapper.PostMapper;
import main.model.Post;
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
    public PostsResponse getPosts(int offset, int limit, String mode) {
        PostsResponse postsResponse = new PostsResponse();
        List<Post> posts = new ArrayList<>();
        int pageNumber = offset / POSTS_ON_PAGE;
        switch (mode) {
            case "recent": {
                Sort dateSort = Sort.by(Sort.Direction.DESC, "time");
                Pageable page = PageRequest.of(pageNumber, limit, dateSort);
                posts = postsRepository.findAllByIsActiveAndModerationStatus(page);
                break;
            }
            case "popular": {
                Pageable page = PageRequest.of(pageNumber, limit);
                posts = postsRepository.findAllByPostCommentCount(page);
                break;
            }
            case "best": {
                Pageable page = PageRequest.of(pageNumber, limit);
                posts = postsRepository.findAllLikedPosts(page);
                break;
            }
            case "early": {
                Sort dateSort = Sort.by(Sort.Direction.ASC, "time");
                Pageable page = PageRequest.of(pageNumber, limit, dateSort);
                posts = postsRepository.findAllByIsActiveAndModerationStatus(page);
                break;
            }
        }
        List<PostDTO> postDTOs = posts.stream().map(mapper::toDTO).collect(Collectors.toList());
        postsResponse.setPosts(postDTOs);
        postsResponse.setCount(postCount());
        return postsResponse;
    }

    @Override
    public PostsResponse getPostsByQuery(int offset, int limit, String query) {
        PostsResponse postsResponse = new PostsResponse();
        int pageNumber = offset / POSTS_ON_PAGE;
        Pageable page = PageRequest.of(pageNumber, limit);
        List<PostDTO> postDTOs = postsRepository.findPostsByIsActiveAndModerationStatusAndTextLike(query, page)
                .stream().map(mapper::toDTO).collect(Collectors.toList());
        postsResponse.setPosts(postDTOs);
        postsResponse.setCount(postCountByQuery(query));
        return postsResponse;
    }

    @Override
    public PostsResponse getPostsByDate(int offset, int limit, String date) {
        PostsResponse postsResponse = new PostsResponse();
        int pageNumber = offset / POSTS_ON_PAGE;
        Pageable page = PageRequest.of(pageNumber, limit);
        List<PostDTO> postDTOs = postsRepository.findPostsByIsActiveAndModerationStatusAndTime(date, page)
                .stream().map(mapper::toDTO).collect(Collectors.toList());
        postsResponse.setPosts(postDTOs);
        postsResponse.setCount(countPostsByDate(date));
        return postsResponse;
    }

    @Override
    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        PostsResponse postsResponse = new PostsResponse();
        int pageNumber = offset / POSTS_ON_PAGE;
        Pageable page = PageRequest.of(pageNumber, limit);
        List<PostDTO> postDTOs = postsRepository.findPostsByTag(tag, page).stream().map(mapper::toDTO).collect(Collectors.toList());
        postsResponse.setPosts(postDTOs);
        postsResponse.setCount(countPostsByTag(tag));
        return postsResponse;
    }

    @Override
    public PostDTO getPostById(int id) {
        if (postsRepository.findById(id).isPresent()) {
            postsRepository.incrementViewCount(id);
            return mapper.toDTO(postsRepository.findById(id).get());
        } else {
            throw new NoSuchPostException("Пост не найден");
        }
    }

    @Override
    public PostsResponse getModerationPosts(int offset, int limit, String status) {
        PostsResponse postsResponse = new PostsResponse();
        List<Post> posts = new ArrayList<>();
        int pageNumber = offset / POSTS_ON_PAGE;
        Pageable page = PageRequest.of(pageNumber, limit);
        int count = 0;
        switch (status) {
            case "new":
                posts = postsRepository.findPostByModerationStatus(ModerationStatus.NEW, page);
                count = postsRepository.countPostsByModerationStatus(ModerationStatus.NEW);
                break;
            case "declined":
                posts = postsRepository.findPostByModerationStatus(ModerationStatus.DECLINED, page);
                count = postsRepository.countPostsByModerationStatus(ModerationStatus.DECLINED);
                break;
            case "accepted":
                posts = postsRepository.findPostByModerationStatus(ModerationStatus.ACCEPTED, page);
                count = postsRepository.countPostsByModerationStatus(ModerationStatus.ACCEPTED);
                break;
        }
        List<PostDTO> postDTOs = posts.stream().map(mapper::toDTO).collect(Collectors.toList());
        postsResponse.setPosts(postDTOs);
        postsResponse.setCount(count);
        return postsResponse;
    }

    @Override
    public CalendarResponse getCalendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setYears(postsRepository.findYearsByPostCount());
        calendarResponse.setPosts(postsRepository.countPostsByYear(year).stream().collect(Collectors.toMap(CalendarDTO::getDate, CalendarDTO::getCount)));
        return calendarResponse;
    }

    private long postCount() {
        return postsRepository.countPostsByIsActiveAndModerationStatus();
    }

    private int postCountByQuery(String query) {
        return postsRepository.countPostsByIsActiveAndModerationStatusAndTextLike(query);
    }

    private int countPostsByDate(String date) {
        return postsRepository.countPostsByIsActiveAndModerationStatusAndTime(date);
    }

    private int countPostsByTag(String tag) {
        return postsRepository.countPostsByTagName(tag);
    }
}
