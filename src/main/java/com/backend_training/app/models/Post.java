package com.backend_training.app.models;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private String content;
    private String userID;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Post(String title, String content, String userID) {
        this.title = title;
        this.content = content;
        this.userID = userID;
    }
}
