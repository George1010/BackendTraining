package com.backend_training.app.utils;

import com.backend_training.app.dto.ErrorDetail;
import com.backend_training.app.exceptions.InvalidPostException;
import com.backend_training.app.models.Post;
import com.backend_training.app.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PostValidator {

    @Autowired
    private PostRepository postRepository;

    public void validatePost(Post post, boolean isUpdate) {
        List<ErrorDetail> errorDetails = new ArrayList<>();

        validateTitle(post.getTitle(), errorDetails);
        validateContent(post.getContent(), errorDetails);
        validateUserID(post.getUserID(), errorDetails);
        if (!errorDetails.isEmpty()) {
            throw new InvalidPostException(errorDetails);
        }

        checkForDuplicatePost(post, isUpdate);
    }

    private void validateTitle(String title, List<ErrorDetail> errorDetails) {
        if (title == null) {
            errorDetails.add(new ErrorDetail("title", "Title cannot be null", "missing_field"));
        } else if (title.length() < 3) {
            errorDetails.add(new ErrorDetail("title", "Title must be at least 3 characters long", "too_short"));
        } else if (title.length() > 100) {
            errorDetails.add(new ErrorDetail("title", "Title cannot exceed 100 characters", "too_long"));
        }
    }

    private void validateContent(String content, List<ErrorDetail> errorDetails) {
        if (content == null) {
            errorDetails.add(new ErrorDetail("content", "Content cannot be null", "missing_field"));
        } else if (content.length() < 10) {
            errorDetails.add(new ErrorDetail("content", "Content must be at least 10 characters long", "too_short"));
        } else if (content.length() > 500) {
            errorDetails.add(new ErrorDetail("content", "Content cannot exceed 500 characters", "too_long"));
        }
    }

    private void validateUserID(String userID, List<ErrorDetail> errorDetails) {
        if (userID == null || userID.isEmpty()) {
            errorDetails.add(new ErrorDetail("userID", "User ID cannot be null or empty", "missing_field"));
        }
    }

    private void checkForDuplicatePost(Post post, boolean isUpdate) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        Optional<Post> existingPost = postRepository.findByTitleAndUserID(post.getTitle(), post.getUserID());
        if (existingPost.isPresent() && (!isUpdate || !existingPost.get().getId().equals(post.getId()))) {
            errorDetails.add(new ErrorDetail(null, "A post with this title already exists for this user", "duplicate_post"));
            throw new InvalidPostException(errorDetails);
        }
    }
}
