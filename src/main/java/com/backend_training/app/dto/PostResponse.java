package com.backend_training.app.dto;

import com.backend_training.app.models.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private List<Post> posts;
    private String nextCursor;
    private String message;
    private List<ErrorDetail> errors;

    public PostResponse(List<Post> posts, String nextCursor) {
        this.posts = posts;
        this.nextCursor = nextCursor;
    }

    public PostResponse(String message, List<ErrorDetail> errors) {
        this.message = message;
        this.errors = errors;
    }
}
