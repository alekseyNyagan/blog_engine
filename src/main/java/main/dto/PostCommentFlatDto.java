package main.dto;

import java.time.Instant;

public record PostCommentFlatDto(
        int id,
        Instant time,
        String text,
        int userId,
        String userName,
        String userPhoto
) {
}
