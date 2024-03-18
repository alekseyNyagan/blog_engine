package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CurrentPostDTO extends AbstractDTO {
    private int id;
    private long timestamp;
    @JsonProperty("active")
    private boolean isActive;
    private IncompleteUserDTO user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<PostCommentDTO> comments;
    private List<String> tags;
}
