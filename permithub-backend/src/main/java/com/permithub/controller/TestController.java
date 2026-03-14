package com.permithub.controller;

import com.permithub.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Public endpoint working!"));
    }

    @GetMapping("/secure")
    public ResponseEntity<ApiResponse<String>> secureEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Secure endpoint working! You are authenticated."));
    }
}