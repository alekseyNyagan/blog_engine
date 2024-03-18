package main.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentDTO extends AbstractDTO {
    private int id;
    private long timestamp;
    private String text;
    private IncompleteUserDTO user;
}
