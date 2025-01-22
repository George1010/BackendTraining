package com.backend_training.app;

import com.backend_training.app.dto.PostResponse;
import com.backend_training.app.exceptions.ResourceNotFoundException;
import com.backend_training.app.models.Post;
import com.backend_training.app.repositories.PostRepository;
import com.backend_training.app.services.PostService;
import com.backend_training.app.utils.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post("Sample Title", "Sample Content", "user123");
        post.setId(UUID.randomUUID());
    }

    @Test
    void createPost_shouldSaveAndReturnPost() {
        //Arrange
        when(postRepository.save(post)).thenReturn(post);

        //Act
        Post createdPost = postService.createPost(post, "user123");

        //Assert
        assertEquals(post, createdPost);
        verify(postValidator).validatePost(post, "user123", false);
        verify(postRepository).save(post);
    }

    @Test
    void getPost_existingPost_shouldReturnPost() {
        //Arrange
        when(postRepository.findById(post.getId())).thenReturn(post);

        //Act
        Post fetchedPost = postService.getPost(post.getId());

        //Assert
        assertEquals(post, fetchedPost);
    }

    @Test
    void getPost_nonExistingPost_shouldThrowException() {
        //Arrange
        when(postRepository.findById(post.getId())).thenReturn(null);

        //Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(post.getId()));
    }

    @Test
    void updatePost_existingPost_shouldUpdateAndReturnPost() {
        //Arrange
        when(postRepository.findById(post.getId())).thenReturn(post);
        post.setTitle("Updated Title");
        post.setContent("Updated Content");

        //Act
        Post updatedPost = postService.updatePost(post, "user123");

        //Assert
        assertEquals("Updated Title", updatedPost.getTitle());
        assertEquals("Updated Content", updatedPost.getContent());
        verify(postValidator).validatePost(post, "user123", true);
        verify(postRepository).save(post);
    }

    @Test
    void updatePost_nonExistingPost_shouldThrowException() {
        //Arrange
        when(postRepository.findById(post.getId())).thenReturn(null);

        //Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(post, "user123"));
    }

    @Test
    void deletePost_nonExistingPost_shouldThrowException() {
        //Arrange
        when(postRepository.findById(post.getId())).thenReturn(null);

        //Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(post.getId(), "user123"));
    }

    @Test
    void deletePost_existingPost_shouldDeleteAndReturnPost() {
        //Arrange
        when(postRepository.findById(post.getId())).thenReturn(post);

        //Act
        Post deletedPost = postService.deletePost(post.getId(), "user123");

        //Assert
        assertEquals(post, deletedPost);
        verify(postRepository).delete(post);
    }

    @Test
    void fetchPosts_noCursor_shouldReturnPostResponse() {
        //Arrange
        List<Post> posts = List.of(post);
        when(postRepository.findTopNPosts(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))).thenReturn(posts);

        //Act
        PostResponse response = postService.fetchPosts(null, 5);

        //Assert
        assertEquals(posts, response.getPosts());
        assertNotNull(response.getNextCursor());
    }

    @Test
    void fetchPosts_withCursor_shouldReturnPostResponse() {
        //Arrange
        List<Post> posts = List.of(post);
        when(postRepository.findByIdLessThan(post.getId(), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))).thenReturn(posts);

        //Act
        PostResponse response = postService.fetchPosts(post.getId().toString(), 5);

        //Assert
        assertEquals(posts, response.getPosts());
        assertNotNull(response.getNextCursor());
    }

    @Test
    void fetchPosts_withLimitGreaterThan20_shouldReturnPostResponseWithLimit20() {
        //Arrange
        List<Post> posts = List.of(post);
        when(postRepository.findTopNPosts(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")))).thenReturn(posts);

        //Act
        PostResponse response = postService.fetchPosts(null, 100);

        //Assert
        assertEquals(posts, response.getPosts());
        assertNotNull(response.getNextCursor());
    }
}
