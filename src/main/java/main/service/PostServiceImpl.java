package main.service;

import main.api.response.CalendarResponse;
import main.api.response.PostsResponse;
import main.dto.CalendarDTO;
import main.dto.CurrentPostDTO;
import main.dto.PostDTO;
import main.exceptions.NoSuchPostException;
import main.mapper.CurrentPostMapper;
import main.mapper.PostMapper;
import main.model.Post;
import main.model.enums.ModerationStatus;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final int POSTS_ON_PAGE = 10;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final PostMapper postMapper;
    private final CurrentPostMapper currentPostMapper;

    @Autowired
    public PostServiceImpl(PostsRepository postsRepository, PostMapper postMapper, UsersRepository usersRepository
            , CurrentPostMapper currentPostMapper) {
        this.postsRepository = postsRepository;
        this.postMapper = postMapper;
        this.usersRepository = usersRepository;
        this.currentPostMapper = currentPostMapper;
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
        List<PostDTO> postDTOs = posts.stream().map(postMapper::toDTO).collect(Collectors.toList());
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
                .stream().map(postMapper::toDTO).collect(Collectors.toList());
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
                .stream().map(postMapper::toDTO).collect(Collectors.toList());
        postsResponse.setPosts(postDTOs);
        postsResponse.setCount(countPostsByDate(date));
        return postsResponse;
    }

    @Override
    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        PostsResponse postsResponse = new PostsResponse();
        int pageNumber = offset / POSTS_ON_PAGE;
        Pageable page = PageRequest.of(pageNumber, limit);
        List<PostDTO> postDTOs = postsRepository.findPostsByTag(tag, page).stream().map(postMapper::toDTO).collect(Collectors.toList());
        postsResponse.setPosts(postDTOs);
        postsResponse.setCount(countPostsByTag(tag));
        return postsResponse;
    }

    @Override
    public CurrentPostDTO getPostById(int id) {
        Optional<Post> post = postsRepository.findById(id);
        if (post.isPresent()) {
            if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
                postsRepository.incrementViewCount(id);
            } else {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                main.model.User currentUser = usersRepository
                        .findUserByEmail(user.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("user" + user.getUsername() + "not found"));
                if (!post.get().getUser().equals(currentUser) && currentUser.isModerator() != 1) {
                    postsRepository.incrementViewCount(id);
                }
            }
            return currentPostMapper.toDTO(post.get());
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
                posts = postsRepository.findPostByModerationStatus(ModerationStatus.NEW.toString(), page);
                count = postsRepository.countPostsByModerationStatus(ModerationStatus.NEW.toString());
                break;
            case "declined":
                posts = postsRepository.findPostByModerationStatus(ModerationStatus.DECLINED.toString(), page);
                count = postsRepository.countPostsByModerationStatus(ModerationStatus.DECLINED.toString());
                break;
            case "accepted":
                posts = postsRepository.findPostByModerationStatus(ModerationStatus.ACCEPTED.toString(), page);
                count = postsRepository.countPostsByModerationStatus(ModerationStatus.ACCEPTED.toString());
                break;
        }
        List<PostDTO> postDTOs = posts.stream().map(postMapper::toDTO).collect(Collectors.toList());
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

    @Override
    public PostsResponse getMyPosts(int offset, int limit, String status) {
        PostsResponse postsResponse = new PostsResponse();
        List<Post> posts = new ArrayList<>();
        int count = 0;
        int pageNumber = offset / POSTS_ON_PAGE;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        main.model.User currentUser = usersRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user" + email + "not fount"));
        Pageable page = PageRequest.of(pageNumber, limit);
        switch (status) {
            case "inactive":
                posts = postsRepository.findPostsByUserAndIsActive(currentUser, (byte) 0, page);
                count = postsRepository.countPostsByUserAndIsActive(currentUser, (byte) 0);
                break;
            case "pending":
                posts = postsRepository.findPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.NEW, page);
                count = postsRepository.countPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.NEW);
                break;
            case "declined":
                posts = postsRepository.findPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.DECLINED, page);
                count = postsRepository.countPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.DECLINED);
                break;
            case "published":
                posts = postsRepository.findPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.ACCEPTED, page);
                count = postsRepository.countPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.ACCEPTED);
                break;
        }
        postsResponse.setPosts(posts.stream().map(postMapper::toDTO).collect(Collectors.toList()));
        postsResponse.setCount(count);
        return postsResponse;
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
