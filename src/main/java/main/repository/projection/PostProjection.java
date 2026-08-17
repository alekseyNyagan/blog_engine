package main.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostProjection {
    Integer id;
    Instant timestamp;
    Integer userId;
    String userName;
    String title;
    String text;
    Long likeCount;
    Long dislikeCount;
    Long commentCount;
    int viewCount;
}
