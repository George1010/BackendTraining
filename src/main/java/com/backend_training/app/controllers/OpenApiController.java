package com.backend_training.app.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/docs")
@CrossOrigin
public class OpenApiController {

    @GetMapping(value = "/openapi", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOpenApiSpec() {
        try {
            ClassPathResource openApiResource = new ClassPathResource("static/openapi.json");
            String openApiSpec = Files.readString(openApiResource.getFile().toPath());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(openApiSpec);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Unable to load OpenAPI specification\"}");
        }
    }
}
