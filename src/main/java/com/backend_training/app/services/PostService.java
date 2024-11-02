package com.backend_training.app.services;

import com.backend_training.app.dto.PostResponse;
import com.backend_training.app.exceptions.InvalidPostException;
import com.backend_training.app.exceptions.ResourceNotFoundException;
import com.backend_training.app.models.Post;
import com.backend_training.app.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    private void validatePost(Post post) {
        if (post.getTitle() == null || post.getTitle().length() < 3 || post.getTitle().length() > 100) {
            throw new InvalidPostException("Title must be between 3 and 100 characters");
        }
        if (post.getContent() == null || post.getContent().length() < 10 || post.getContent().length() > 500) {
            throw new InvalidPostException("Content must have at least 10 characters");
        }
        if (post.getUserID() == null || post.getUserID().isEmpty()) {
            throw new InvalidPostException("User ID cannot be null or empty");
        }
    }

    public Post createPost(Post post) {
        validatePost(post);
        postRepository.save(post);
        return post;
    }

    public Post updatePost(Post post) {
        Post existingPost = postRepository.findById(post.getId());
        if (existingPost != null && !existingPost.isDeleted()) {
            validatePost(post);
            if (post.getTitle() != null) {
                existingPost.setTitle(post.getTitle());
            }
            if (post.getContent() != null) {
                existingPost.setContent(post.getContent());
            }
            postRepository.save(existingPost);
            return existingPost;
        } else {
            throw new ResourceNotFoundException("Post not found");
        }
    }

    public Post deletePost(UUID id) {
        Post post = postRepository.findById(id);
        if (post != null) {
            postRepository.delete(post);
            return post;
        } else {
            throw new ResourceNotFoundException("Post not found");
        }
    }


    public Post getPost(UUID id) {
        Post post =  postRepository.findById(id);
        if (post != null) {
            return post;
        } else {
            throw new ResourceNotFoundException("Post not found");
        }
    }

    public PostResponse fetchPosts(String cursor, int limit) {

        List<Post> posts;

        if (cursor == null) {
            posts = postRepository.findTopNPosts(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")));
        } else {
            posts = postRepository.findByIdLessThan(UUID.fromString(cursor), PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")));
        }

        String nextCursor = posts.isEmpty() ? null : String.valueOf(posts.get(posts.size() - 1).getId());

        return new PostResponse(posts, nextCursor);
    }
}