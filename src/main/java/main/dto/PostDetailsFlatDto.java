package main.dto;

import java.time.Instant;

public record PostDetailsFlatDto(
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
