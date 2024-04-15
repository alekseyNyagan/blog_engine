package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "Response to the client with years and posts")
@Data
public class CalendarResponse {
    @Schema(description = "List of years in which at least one post was published")
    private List<Integer> years;
    @Schema(description = "Map of years in string format with posts count")
    private Map<String, Integer> posts;
}
