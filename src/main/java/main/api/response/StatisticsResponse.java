package main.api.response;

public interface StatisticsResponse {
    public Integer getPostsCount();

    public Integer getLikesCount();

    public Integer getDislikesCount();

    public Integer getViewsCount();

    public Long getFirstPublication();
}
