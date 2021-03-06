package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class GlobalSettingsResponse {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiuserMode;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;

    public boolean isMultiuserMode() {
        return multiuserMode;
    }

    public void setMultiuserMode(boolean multiuserMode) {
        this.multiuserMode = multiuserMode;
    }

    public boolean isPostPremoderation() {
        return postPremoderation;
    }

    public void setPostPremoderation(boolean postPremoderation) {
        this.postPremoderation = postPremoderation;
    }

    public boolean isStatisticsIsPublic() {
        return statisticsIsPublic;
    }

    public void setStatisticsIsPublic(boolean statisticsIsPublic) {
        this.statisticsIsPublic = statisticsIsPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalSettingsResponse that = (GlobalSettingsResponse) o;
        return multiuserMode == that.multiuserMode && postPremoderation == that.postPremoderation && statisticsIsPublic == that.statisticsIsPublic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(multiuserMode, postPremoderation, statisticsIsPublic);
    }
}
