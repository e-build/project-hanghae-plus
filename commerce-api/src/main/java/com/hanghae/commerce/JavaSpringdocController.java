package com.hanghae.commerce;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/springdoc-test/java")
public class JavaSpringdocController {

    @PostMapping
    public ResponseEntity<String> post(
        @RequestParam String name,
        @RequestParam(required = false) Double point,
        @RequestParam(required = false) Integer age,
        @RequestParam(required = false) Boolean boolValue
    ) {
        return ResponseEntity.ok("success");
    }
}
