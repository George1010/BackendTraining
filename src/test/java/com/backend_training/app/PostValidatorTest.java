package com.backend_training.app;

import com.backend_training.app.exceptions.InvalidPostException;
import com.backend_training.app.models.Post;
import com.backend_training.app.repositories.PostRepository;
import com.backend_training.app.utils.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

    @InjectMocks
    private PostValidator postValidator;

    @Mock
    private PostRepository postRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post("Valid Title", "Valid Content", "user123");
        post.setId(UUID.randomUUID());
    }

    @Test
    public void validatePost_Success() {
        //Arrange
        post.setContent("a".repeat(500));
        post.setTitle("a".repeat(100));
        when(postRepository.findByTitleAndUserID(post.getTitle(), post.getUserID())).thenReturn(Optional.empty());

        //Act & Assert
        assertDoesNotThrow(() -> postValidator.validatePost(post, false));
    }

    @Test
    void validatePost_nullTitle_shouldThrowInvalidPostException() {
        //Arrange
        post.setTitle(null);

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getField().equals("title") && e.getCode().equals("missing_field")));
    }

    @Test
    public void validatePost_ShortTitle_shouldThrowInvalidPostException() {
        //Arrange
        post.setTitle("Hi");

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getField().equals("title") && e.getMessage().equals("Title must be at least 3 characters long")));
    }

    @Test
    public void validatePost_LongTitle_shouldThrowInvalidPostException() {
        //Arrange
        post.setTitle("A".repeat(101));

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getField().equals("title") && e.getMessage().equals("Title cannot exceed 100 characters")));
    }

    @Test
    void validatePost_nullContent_shouldThrowInvalidPostException() {
        //Arrange
        post.setContent(null);

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getField().equals("content") && e.getCode().equals("missing_field")));
    }

    @Test
    public void validatePost_ShortContent_shouldThrowInvalidPostException() {
        //Arrange
        post.setContent("Hi");

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getField().equals("content") && e.getMessage().equals("Content must be at least 10 characters long")));
    }

    @Test
    public void validatePost_LongContent_shouldThrowInvalidPostException() {
        //Arrange
        post.setContent("A".repeat(501));

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getField().equals("content") && e.getMessage().equals("Content cannot exceed 500 characters")));
    }

    @Test
    void validatePost_nullUserID_shouldThrowInvalidPostException() {
        //Arrange
        post.setUserID(null);

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getField().equals("userID") && e.getCode().equals("missing_field")));
    }

    @Test
    void validatePost_emptyUserID_shouldThrowInvalidPostException() {
        //Arrange
        post.setUserID("");

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getField().equals("userID") && e.getCode().equals("missing_field")));
    }

    @Test
    void validatePost_duplicatePostSameIDCreate_shouldThrowInvalidPostException() {
        //Arrange
        when(postRepository.findByTitleAndUserID(post.getTitle(), post.getUserID())).thenReturn(Optional.of(post));

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, false));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getCode().equals("duplicate_post")));
    }

    @Test
    void validatePost_duplicatePostSameIDUpdate_shouldThrowInvalidPostException() {
        //Arrange
        when(postRepository.findByTitleAndUserID(post.getTitle(), post.getUserID())).thenReturn(Optional.of(post));

        //Act & Assert
        assertDoesNotThrow(() -> postValidator.validatePost(post, true));
    }

    @Test
    void validatePost_duplicatePostWithDifferentID_shouldThrowInvalidPostException() {
        //Arrange
        Post existingPost = new Post("Valid Title", "Valid Content", "user123");
        existingPost.setId(UUID.randomUUID());
        when(postRepository.findByTitleAndUserID(post.getTitle(), post.getUserID())).thenReturn(Optional.of(existingPost));

        //Act & Assert
        InvalidPostException exception = assertThrows(InvalidPostException.class, () -> postValidator.validatePost(post, true));
        assertTrue(exception.getErrorDetails().stream().anyMatch(e -> e.getCode().equals("duplicate_post")));
    }
}
