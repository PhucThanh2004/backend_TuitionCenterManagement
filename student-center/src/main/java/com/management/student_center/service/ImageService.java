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
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload!", e);
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
            String fileName = System.currentTimeMillis()
                    + "_"
                    + (originalFileName != null
                    ? originalFileName.replaceAll("\\s+", "_")
                    : "avatar");

            // uploads/avatar/xxx.png
            Path destinationFile = rootLocation.resolve(fileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // return uploads/avatar/xxx.png
            return UPLOAD_DIR + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Lưu avatar thất bại!", e);
        }
    }

    /**
     * Delete avatar - Xử lý cả URL và đường dẫn local
     */
    public void deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        // Bỏ qua nếu là URL (http:// hoặc https://)
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return;
        }

        try {
            Path filePath;
            
            // Nếu path bắt đầu bằng "uploads/" hoặc "/uploads/"
            if (imagePath.startsWith("uploads/") || imagePath.startsWith("/uploads/")) {
                String cleanPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
                filePath = Paths.get(cleanPath);
            } else {
                // Xóa từ thư mục uploads
                filePath = rootLocation.resolve(imagePath);
            }
            
            Files.deleteIfExists(filePath);
            
        } catch (InvalidPathException | IOException e) {
            // Bỏ qua lỗi, không in log
        }
    }

    /**
     * Kiểm tra xem đường dẫn có phải là URL không
     */
    public boolean isUrl(String imagePath) {
        return imagePath != null && 
               (imagePath.startsWith("http://") || imagePath.startsWith("https://"));
    }
}