package com.management.student_center.assistant.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PromptLoader {

    public String loadPrompt(String path) {

        try {

            ClassPathResource resource = new ClassPathResource(path);

            byte[] bytes = resource.getInputStream().readAllBytes();

            return new String(bytes, StandardCharsets.UTF_8);

        } catch (IOException e) {

            throw new RuntimeException("Cannot load prompt file: " + path);

        }

    }

}