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
    private String errorMessage;

    public PostResponse(List<Post> posts, String nextCursor) {
        this.posts = posts;
        this.nextCursor = nextCursor;
    }
}
