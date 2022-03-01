package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.dto.CalendarDTO;
import main.dto.CurrentPostDTO;
import main.dto.PostDTO;
import main.error.AbstractError;
import main.error.ImageError;
import main.error.TextError;
import main.error.TitleError;
import main.exceptions.NoSuchPostException;
import main.mapper.CurrentPostMapper;
import main.mapper.PostMapper;
import main.model.Post;
import main.model.PostVote;
import main.model.Tag;
import main.model.enums.ModerationStatus;
import main.repository.PostVotesRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final int MAX_FILE_SIZE = 5_242_880;
    private static final int POSTS_ON_PAGE = 10;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final PostVotesRepository postVotesRepository;
    private final PostMapper postMapper;
    private final CurrentPostMapper currentPostMapper;

    @Autowired
    public PostServiceImpl(PostsRepository postsRepository, PostMapper postMapper, UsersRepository usersRepository
            , CurrentPostMapper currentPostMapper, PostVotesRepository postVotesRepository) {
        this.postsRepository = postsRepository;
        this.postMapper = postMapper;
        this.usersRepository = usersRepository;
        this.currentPostMapper = currentPostMapper;
        this.postVotesRepository = postVotesRepository;
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

    @Override
    public ErrorsResponse addPost(PostRequest postRequest, boolean postPremoderation) {
        return createPost(0, postRequest, postPremoderation);
    }

    @Override
    public ErrorsResponse updatePost(int id, PostRequest postRequest, boolean postPremoderation) {
        return createPost(id, postRequest, postPremoderation);
    }

    @Override
    public ResultResponse like(PostVoteRequest postVoteRequest) {
        ResultResponse resultResponse = new ResultResponse();
        main.model.User currentUser = getAuthUser();
        Post post = postsRepository.findById(postVoteRequest.getPostId()).get();
        Optional<PostVote> postVote = postVotesRepository.findPostVoteByUserAndPost(currentUser, post);
        if (postVote.isEmpty()) {
            PostVote newPostVote = new PostVote();
            newPostVote.setPost(post);
            newPostVote.setUser(currentUser);
            newPostVote.setTime(new Date());
            newPostVote.setValue((byte) 1);
            postVotesRepository.save(newPostVote);
            resultResponse.setResult(true);
        } else if (postVote.get().getValue() == -1) {
            postVotesRepository.updatePostVote((byte) 1, currentUser.getId(), post.getId());
            resultResponse.setResult(true);
        }
        return resultResponse;
    }

    @Override
    public ResultResponse dislike(PostVoteRequest postVoteRequest) {
        ResultResponse resultResponse = new ResultResponse();
        main.model.User currentUser = getAuthUser();
        Post post = postsRepository.findById(postVoteRequest.getPostId()).get();
        Optional<PostVote> postVote = postVotesRepository.findPostVoteByUserAndPost(currentUser, post);
        if (postVote.isEmpty()) {
            PostVote newPostVote = new PostVote();
            newPostVote.setPost(post);
            newPostVote.setUser(currentUser);
            newPostVote.setTime(new Date());
            newPostVote.setValue((byte) -1);
            postVotesRepository.save(newPostVote);
            resultResponse.setResult(true);
        } else if (postVote.get().getValue() == 1) {
            postVotesRepository.updatePostVote((byte) -1, currentUser.getId(), post.getId());
            resultResponse.setResult(true);
        }
        return resultResponse;
    }

    @Override
    public StatisticsResponse getMyStatistic() {
        main.model.User currentUser = getAuthUser();
        return postsRepository.getMyStatistic(currentUser.getId());
    }

    @Override
    public StatisticsResponse getAllStatistic() {
        return postsRepository.getAllStatistic();
    }

    @Override
    public ResultResponse moderation(ModerationRequest moderationRequest) {
        ResultResponse resultResponse = new ResultResponse();
        Optional<Post> post = postsRepository.findById(moderationRequest.getPostId());
        main.model.User moderator = getAuthUser();
        if (moderationRequest.getDecision().equals("accept")) {
            postsRepository.updateModerationStatus("ACCEPTED", moderator.getId(), post.get().getId());
        } else if (moderationRequest.getDecision().equals("decline")) {
            postsRepository.updateModerationStatus("DECLINED", moderator.getId(), post.get().getId());
        }
        resultResponse.setResult(true);
        return resultResponse;
    }

    @Override
    public Object image(MultipartFile multipartFile) throws IOException {
        String contentType = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (multipartFile.getSize() < MAX_FILE_SIZE && (contentType.equals("png") || contentType.equals("jpg"))) {
            String hash = generateRandomHash(5);
            String path = "./upload/ab/cd/ef/" + hash + "." + contentType;
            FileOutputStream outputStream = new FileOutputStream(path);
            outputStream.write(multipartFile.getBytes());
            return "/upload/ab/cd/ef/" + hash + "." + contentType;
        } else {
            ErrorsResponse errorsResponse = new ErrorsResponse();
            Map<AbstractError, String> errors = new HashMap<>();
            if (multipartFile.getSize() > MAX_FILE_SIZE) {
                ImageError imageError = new ImageError("Размер файла превышает допустимый размер");
                errors.put(imageError, imageError.getMessage());
            }
            ImageError imageError = new ImageError("Неверный формат изображения! Изображение должно быть в формате png или jpg");
            errors.put(imageError, imageError.getMessage());
            errorsResponse.setResult(false);
            errorsResponse.setErrors(errors);
            return errorsResponse;
        }
    }

    private ErrorsResponse createPost(int id, PostRequest postRequest, boolean postPremoderation) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<AbstractError, String> errors = new HashMap<>();
        String title = postRequest.getTitle();
        String text = postRequest.getText();
        String htmlTagRegexp = "\\<.*?\\>";
        main.model.User currentUser = getAuthUser();
        if (text.replaceAll(htmlTagRegexp, "").length() > 50 && title.length() > 3) {
            Post post = new Post();
            List<Tag> tags = new ArrayList<>();
            post.setIsActive(postRequest.getActive());
            post.setTitle(postRequest.getTitle());
            post.setText(postRequest.getText());
            post.setUser(currentUser);
            postRequest.getTags().forEach(t -> tags.add(new Tag(t)));
            post.setTags(tags);
            if (id != 0) {
                post.setId(id);
            }
            if (postPremoderation) {
                if (currentUser.isModerator() != 1) {
                    post.setModerationStatus(ModerationStatus.NEW);
                } else {
                    post.setModerationStatus(ModerationStatus.ACCEPTED);
                }
            } else {
                post.setModerationStatus(ModerationStatus.ACCEPTED);
            }
            if (postRequest.getTimestamp() <= new Date().getTime()) {
                post.setTime(new Date());
            } else {
                post.setTime(new Date(postRequest.getTimestamp()));
            }
            errorsResponse.setResult(true);
            postsRepository.save(post);
        } else {
            if (title.isEmpty()) {
                TitleError titleError = new TitleError("Заголовок не установлен");
                errors.put(titleError, titleError.getMessage());
            } else if (title.length() < 3) {
                TitleError titleError = new TitleError("Заголовок слишком короткий");
                errors.put(titleError, titleError.getMessage());
            }
            if (text.replaceAll(htmlTagRegexp, "").isEmpty()) {
                TextError textError = new TextError("Текст публикации пустой");
                errors.put(textError, textError.getMessage());
            } else if (text.replaceAll(htmlTagRegexp, "").length() < 50) {
                TextError textError = new TextError("Текст публикации слишком короткий");
                errors.put(textError, textError.getMessage());
            }
            errorsResponse.setErrors(errors);
        }
        return errorsResponse;
    }

    private main.model.User getAuthUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        return usersRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user" + email + "not fount"));
    }

    private String generateRandomHash(int length) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
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
