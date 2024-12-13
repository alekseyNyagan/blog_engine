package main.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    @Schema(description = "Date and time of publication post in UTC format")
    private long timestamp;
    @Schema(description = "Opened or closed post. 1 - open, 0 - closed")
    private byte active;
    @Schema(description = "Title of post")
    @NotBlank(message = "Заголовок не установлен")
    @Size(min = 3, message = "Заголовок слишком короткий")
    private String title;
    @Schema(description = "Tags of post")
    private List<String> tags;
    @Schema(description = "text of post in html format")
    @NotBlank(message = "Текст публикации пустой")
    @Size(min = 3, message = "Текст публикации слишком короткий")
    private String text;
}
