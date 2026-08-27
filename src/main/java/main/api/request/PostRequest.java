package main.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PostRequest(@Schema(description = "Date and time of publication post in UTC format") long timestamp,
                          @Schema(description = "Opened or closed post. 1 - open, 0 - closed") byte active,
                          @Schema(description = "Title of post") @NotBlank(message = "Заголовок не установлен") @Size(min = 3, message = "Заголовок слишком короткий") String title,
                          @Schema(description = "Tags of post") Set<String> tags,
                          @Schema(description = "text of post in html format") @NotBlank(message = "Текст публикации пустой") @Size(min = 3, message = "Текст публикации слишком короткий") String text) {
}
