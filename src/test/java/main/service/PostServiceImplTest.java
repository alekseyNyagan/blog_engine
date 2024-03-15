package main.service;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import main.api.response.PostsResponse;
import main.dto.PostDTO;
import main.mapper.PostMapper;
import main.model.*;
import main.model.enums.ModerationStatus;
import main.repository.PostsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class PostServiceImplTest {
    private List<Post> posts;
    List<PostDTO> postDTOS;

    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper mapper;

    @MockBean
    private PostsRepository postsRepository;

    @BeforeEach
    public void setUp() {
        Fixture.of(User.class).addTemplate("user", new Rule() {{
            add("id", random(Integer.class, range(1, 200)));
            add("isModerator", random(Byte.class, range(0, 1)));
            add("regTime", randomDate("2020-01-01", "2023-01-01", new SimpleDateFormat("yyyy-MM-dd")));
            add("name", firstName());
            add("email", "${name}@gmail.com");
            add("password", lastName());
        }});
        Fixture.of(PostComment.class).addTemplate("postComment", new Rule() {{
            add("id", random(Integer.class, range(1, 100)));
            add("user", one(User.class, "user"));
            add("time", randomDate("2020-01-01", "2023-01-01", new SimpleDateFormat("yyyy-MM-dd")));
            add("text", regex("[a-zA-Z]{10,50}"));
        }});
        Fixture.of(PostVote.class).addTemplate("postVote", new Rule() {{
            add("user", one(User.class, "user"));
            add("time", randomDate("2020-01-01", "2023-01-01", new SimpleDateFormat("yyyy-MM-dd")));
            add("value", uniqueRandom("-1", "1"));
        }});
        Fixture.of(Tag.class).addTemplate("tag", new Rule() {{
            add("id", random(Integer.class, range(1, 10)));
            add("name", regex("[a-zA-z]{1,5}"));
        }});
        Fixture.of(Post.class).addTemplate("post", new Rule() {{
            add("id", random(Integer.class, range(1, 50)));
            add("isActive", random(Byte.class, range(0, 1)));
            add("moderationStatus", uniqueRandom(ModerationStatus.NEW, ModerationStatus.ACCEPTED, ModerationStatus.DECLINED));
            add("user", one(User.class, "user"));
            add("time", randomDate("2020-01-01", "2023-01-01", new SimpleDateFormat("yyyy-MM-dd")));
            add("title", regex("[a-zA-Z]{10,20}"));
            add("text", regex("[a-zA-Z]{10,50}"));
            add("viewCount", random(Integer.class, range(0, 100)));
            add("comments", has(20).of(PostComment.class, "postComment"));
            add("votes", has(10).of(PostVote.class, "postVote"));
            add("tags", has(5).of(Tag.class, "tag"));
        }});
        posts = Fixture.from(Post.class).gimme(10, "post");
        Mockito.when(postsRepository.findById(posts.get(0).getId())).thenReturn(Optional.of(posts.get(0)));
        Mockito.when(postsRepository.findById(posts.get(1).getId())).thenReturn(Optional.of(posts.get(1)));
        Mockito.when(postsRepository.findById(posts.get(2).getId())).thenReturn(Optional.of(posts.get(2)));
        Mockito.when(postsRepository.findById(posts.get(3).getId())).thenReturn(Optional.of(posts.get(3)));
        Mockito.when(postsRepository.findById(posts.get(4).getId())).thenReturn(Optional.of(posts.get(4)));
        Mockito.when(postsRepository.findById(posts.get(5).getId())).thenReturn(Optional.of(posts.get(5)));
        Mockito.when(postsRepository.findById(posts.get(6).getId())).thenReturn(Optional.of(posts.get(6)));
        Mockito.when(postsRepository.findById(posts.get(7).getId())).thenReturn(Optional.of(posts.get(7)));
        Mockito.when(postsRepository.findById(posts.get(8).getId())).thenReturn(Optional.of(posts.get(8)));
        Mockito.when(postsRepository.findById(posts.get(9).getId())).thenReturn(Optional.of(posts.get(9)));
        Mockito.when(postsRepository.countPostsByIsActiveAndModerationStatus()).thenReturn(10);
    }

    @Test
    public void getPostsShouldReturnSortedListByTimeDescending() {
        PostsResponse expected = new PostsResponse();
        posts.sort(((o1, o2) -> {
            if (o1.getTime().isAfter(o2.getTime())) {
                return 1;
            } else if (o1.getTime().isBefore(o2.getTime())) {
                return -1;
            } else {
                return 0;
            }
        }));
        postDTOS = posts.stream().map(mapper::toDTO).collect(Collectors.toList());
        expected.setPosts(postDTOS);
        expected.setCount(10);
        Mockito.when(postsRepository.findAllByIsActiveAndModerationStatus(Mockito.any(Pageable.class))).thenReturn(posts);

        PostsResponse actual = postService.getPosts(0, 10, "recent");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getPostsShouldReturnSortedListByCommentsCount() {
        PostsResponse expected = new PostsResponse();
        posts.sort((Comparator.comparingInt(o -> o.getComments().size())));
        postDTOS = posts.stream().map(mapper::toDTO).collect(Collectors.toList());
        expected.setPosts(postDTOS);
        expected.setCount(10);
        Mockito.when(postsRepository.findAllByPostCommentCount(Mockito.any(Pageable.class))).thenReturn(posts);

        PostsResponse actual = postService.getPosts(0, 10, "popular");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getPostsShouldReturnSortedListByLikes() {
        PostsResponse expected = new PostsResponse();
        posts.sort(((o1, o2) -> {
            if (o1.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count() >
                    o2.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count()) {
                return 1;
            } else if (o1.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count() <
                    o2.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count()) {
                return -1;
            } else {
                return 0;
            }
        }));
        postDTOS = posts.stream().map(mapper::toDTO).collect(Collectors.toList());
        expected.setPosts(postDTOS);
        expected.setCount(10);
        Mockito.when(postsRepository.findAllLikedPosts(Mockito.any(Pageable.class))).thenReturn(posts);

        PostsResponse actual = postService.getPosts(0, 10, "best");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getPostsShouldReturnSortedListByTimeAscending() {
        PostsResponse expected = new PostsResponse();
        posts.sort(((o1, o2) -> {
            if (o1.getTime().isBefore(o2.getTime())) {
                return 1;
            } else if (o1.getTime().isAfter(o2.getTime())) {
                return -1;
            } else {
                return 0;
            }
        }));
        postDTOS = posts.stream().map(mapper::toDTO).collect(Collectors.toList());
        expected.setPosts(postDTOS);
        expected.setCount(10);
        Mockito.when(postsRepository.findAllByIsActiveAndModerationStatus(Mockito.any(Pageable.class))).thenReturn(posts);

        PostsResponse actual = postService.getPosts(0, 10, "early");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getModerationPostsShouldReturnNewPosts() {
        PostsResponse expected = new PostsResponse();
        List<Post> newPosts = posts.stream().filter(post -> post.getModerationStatus().equals(ModerationStatus.NEW)).collect(Collectors.toList());
        postDTOS = newPosts.stream().map(mapper::toDTO).collect(Collectors.toList());
        expected.setPosts(postDTOS);
        expected.setCount(10);
        Mockito.when(postsRepository.findPostByModerationStatus(ModerationStatus.NEW, PageRequest.of(0, 10))).thenReturn(newPosts);
        Mockito.when(postsRepository.countPostsByModerationStatus(ModerationStatus.NEW)).thenReturn(10);

        PostsResponse actual = postService.getModerationPosts(0, 10, "new");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getModerationPostsShouldReturnAcceptedPosts() {
        PostsResponse expected = new PostsResponse();
        List<Post> acceptedPosts = posts.stream().filter(post -> post.getModerationStatus().equals(ModerationStatus.ACCEPTED)).collect(Collectors.toList());
        postDTOS = acceptedPosts.stream().map(mapper::toDTO).collect(Collectors.toList());
        expected.setPosts(postDTOS);
        expected.setCount(10);
        Mockito.when(postsRepository.findPostByModerationStatus(ModerationStatus.ACCEPTED, PageRequest.of(0, 10))).thenReturn(acceptedPosts);
        Mockito.when(postsRepository.countPostsByModerationStatus(ModerationStatus.ACCEPTED)).thenReturn(10);

        PostsResponse actual = postService.getModerationPosts(0, 10, "accepted");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getModerationPostsShouldReturnDeclinedPosts() {
        PostsResponse expected = new PostsResponse();
        List<Post> declinedPosts = posts.stream().filter(post -> post.getModerationStatus().equals(ModerationStatus.DECLINED)).collect(Collectors.toList());
        postDTOS = declinedPosts.stream().map(mapper::toDTO).collect(Collectors.toList());
        expected.setPosts(postDTOS);
        expected.setCount(10);
        Mockito.when(postsRepository.findPostByModerationStatus(ModerationStatus.DECLINED, PageRequest.of(0, 10))).thenReturn(declinedPosts);
        Mockito.when(postsRepository.countPostsByModerationStatus(ModerationStatus.DECLINED)).thenReturn(10);

        PostsResponse actual = postService.getModerationPosts(0, 10, "declined");
        Assertions.assertEquals(expected, actual);
    }
}
