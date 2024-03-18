package main.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostDTO extends AbstractDTO {
    private int id;
    private long timestamp;
    private IncompleteUserDTO user;
    private String title;
    private String announce;
    private int likeCount;
    private int commentCount;
    private int dislikeCount;
    private int viewCount;
}
