package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SettingsRequest {
    @Schema(description = "If this setting is enabled, new users will be able to register. Otherwise, they will not.")
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiuserMode;
    @Schema(description = "If this setting is enabled, posts should be moderated.")
    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;
    @Schema(description = """
            If this setting is enabled, all users can view the statistics page.
            Otherwise, only admins can view the statistics page.
            """)
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;
}
