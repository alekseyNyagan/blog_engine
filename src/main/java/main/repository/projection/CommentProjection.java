package main.repository.projection;

import java.time.Instant;

public record CommentProjection(
        int id,
        Instant time,
        String text,
        int userId,
        String userName,
        String userPhoto
) {
}
