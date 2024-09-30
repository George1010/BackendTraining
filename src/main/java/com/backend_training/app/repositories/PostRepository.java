package com.backend_training.app.repositories;


import com.backend_training.app.models.Post;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@ComponentScan(basePackages = "com.backend_training.app")
public interface PostRepository extends JpaRepository<Post, String> {
    List<Post> findByIdLessThan(UUID cursor, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findTopNPosts(PageRequest createdAt);
}