package com.backend_training.app.services;

import com.backend_training.app.dto.PostResponse;
import com.backend_training.app.exceptions.ResourceNotFoundException;
import com.backend_training.app.models.Post;
import com.backend_training.app.repositories.PostRepository;
import com.backend_training.app.utils.PostValidator;
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

    @Autowired
    private PostValidator postValidator;


    public Post createPost(Post post) {
        postValidator.validatePost(post, false);
        postRepository.save(post);
        return post;
    }

    public Post updatePost(Post post) {
        Post existingPost = postRepository.findById(post.getId());
        if (existingPost != null) {
            postValidator.validatePost(post, true);
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
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
        limit = Math.min(limit, 20);

        if (cursor == null) {
            posts = postRepository.findTopNPosts(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")));
        } else {
            posts = postRepository.findByIdLessThan(UUID.fromString(cursor), PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")));
        }

        String nextCursor = posts.isEmpty() ? null : String.valueOf(posts.get(posts.size() - 1).getId());

        return new PostResponse(posts, nextCursor);
    }
}