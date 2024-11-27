package com.backend_training.app.dto;

import lombok.Data;

@Data
public class User {
    private String email;
    private String password;
    private String userName;
}