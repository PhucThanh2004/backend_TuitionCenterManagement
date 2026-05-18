package com.management.student_center.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
public class ImageService {

    // uploads/avatar
    private static final String UPLOAD_DIR = "uploads/avatars";

    // root path
    private final Path rootLocation;

    public ImageService() {

        // uploads/avatar
        this.rootLocation = Paths.get(UPLOAD_DIR);
    }

    @PostConstruct
    public void init() {

        try {

            Files.createDirectories(rootLocation);

            System.out.println(
                    "Upload folder: "
                    + rootLocation.toAbsolutePath()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Không thể tạo thư mục upload!",
                    e
            );
        }
    }

    /**
     * Save avatar
     */
    public String saveImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {

            String originalFileName = file.getOriginalFilename();

            String fileName =
                    System.currentTimeMillis()
                    + "_"
                    + (originalFileName != null
                    ? originalFileName.replaceAll("\\s+", "_")
                    : "avatar");

            // uploads/avatar/xxx.png
            Path destinationFile =
                    rootLocation.resolve(fileName);

            try (InputStream inputStream = file.getInputStream()) {

                Files.copy(
                        inputStream,
                        destinationFile,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            // return uploads/avatar/xxx.png
            return UPLOAD_DIR + "/" + fileName;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Lưu avatar thất bại!",
                    e
            );
        }
    }

    /**
     * Delete avatar
     */
    public void deleteImage(String relativePath) {

        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }

        try {

            Path filePath = Paths.get(relativePath);

            Files.deleteIfExists(filePath);

        } catch (IOException e) {

            System.err.println(
                    "Xóa file thất bại: "
                    + relativePath
                    + " | "
                    + e.getMessage()
            );
        }
    }
}