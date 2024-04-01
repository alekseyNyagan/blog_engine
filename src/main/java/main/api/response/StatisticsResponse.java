package main.api.response;

public interface StatisticsResponse {
    Integer getPostsCount();

    Integer getLikesCount();

    Integer getDislikesCount();

    Integer getViewsCount();

    Long getFirstPublication();
}
