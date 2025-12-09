// service/ImageService.java
package com.management.student_center.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
public class ImageService {

    // Giống "public" trong code JS
    private final String PUBLIC_DIR = "public";
    
    // Giống "subDir" (mặc định là "uploads")
    private final String UPLOAD_SUB_DIR = "uploads";

    // Đường dẫn gốc đầy đủ (public/uploads)
    private final Path rootLocation;

    public ImageService() {
        // Path.of() tự động dùng / hoặc \ tùy HĐH
        this.rootLocation = Paths.get(PUBLIC_DIR, UPLOAD_SUB_DIR);
    }

    // Hàm này chạy khi Service khởi động, giống "fs.mkdirSync(recursive: true)"
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            System.out.println("Đã tạo thư mục (nếu chưa có): " + rootLocation.toString());
        } catch (IOException e) {
            throw new RuntimeException("Không thể khởi tạo thư mục lưu trữ!", e);
        }
    }

    /**
     * Tương đương saveImage
     * @param file Đối tượng MultipartFile từ controller
     * @return Đường dẫn tương đối (ví dụ: "uploads/12345_image.png")
     */
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 1. Tạo tên file duy nhất (giống Date.now()_...)
            String originalFileName = file.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + (originalFileName != null ? originalFileName : "file");
            
            // 2. Resolve đường dẫn (giống path.join)
            Path destinationFile = this.rootLocation.resolve(fileName);
            
            // 3. Ghi file (giống fs.writeFileSync / fs.renameSync)
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // 4. Trả về đường dẫn tương đối (giống `${subDir}/${fileName}`)
            // Chúng ta trả về cả subDir để WebConfig có thể map
            return UPLOAD_SUB_DIR + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Lưu file thất bại!", e);
        }
    }

    /**
     * Tương đương deleteImage
     * @param relativePath Đường dẫn tương đối (ví dụ: "uploads/12345_image.png")
     */
    public void deleteImage(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }

        try {
            // 1. Resolve đường dẫn (giống path.join(process.cwd(), "public", relativePath))
            Path filePath = Paths.get(PUBLIC_DIR).resolve(relativePath);

            // 2. Xóa file (giống fs.unlinkSync)
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            System.err.println("Xóa file thất bại: " + relativePath + ". Lỗi: " + e.getMessage());
        }
    }
}