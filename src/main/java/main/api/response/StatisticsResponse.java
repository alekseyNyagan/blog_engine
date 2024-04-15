package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response to the client with statistics")
public interface StatisticsResponse {
    @Schema(description = "Posts count")
    Integer getPostsCount();

    @Schema(description = "Likes count")
    Integer getLikesCount();

    @Schema(description = "Dislikes count")
    Integer getDislikesCount();

    @Schema(description = "Views count")
    Integer getViewsCount();

    @Schema(description = "First publication date in UTC format")
    Long getFirstPublication();
}
