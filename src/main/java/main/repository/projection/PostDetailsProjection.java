package main.repository.projection;

import java.time.Instant;

public record PostDetailsProjection(
        int id,
        Instant time,
        boolean active,
        int userId,
        String userName,
        String userPhoto,
        String title,
        String text,
        long likeCount,
        long dislikeCount,
        int viewCount,
        String email
) {
}
