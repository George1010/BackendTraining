package com.backend_training.app.controllers;

import com.backend_training.app.models.Post;
import com.backend_training.app.ratelimiter.RateLimit;
import com.backend_training.app.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resources/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "10") int limit, 
                                    @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(postService.fetchPosts(cursor, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PostMapping
    @RateLimit(capacity = 10, refillTokens = 1, duration = 60 * 1000)
    public ResponseEntity<?> createPost(@AuthenticationPrincipal Jwt jwt, 
                                      @RequestBody Post post) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(postService.createPost(post, userId));
    }

    @PutMapping
    public ResponseEntity<?> updatePost(@AuthenticationPrincipal Jwt jwt,
                                      @RequestBody Post post) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(postService.updatePost(post, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal Jwt jwt,
                                      @PathVariable UUID id) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(postService.deletePost(id, userId));
    }
}