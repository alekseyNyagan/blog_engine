package main.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RestoreRequest(@Schema(description = "Email of user to send restore password info") String email) {
}
