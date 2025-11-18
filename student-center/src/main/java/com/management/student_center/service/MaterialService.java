package com.management.student_center.service;

import com.management.student_center.dto.MaterialDTO;
import com.management.student_center.entity.Material;
import com.management.student_center.entity.Subject;
import com.management.student_center.entity.User;
import com.management.student_center.repository.MaterialRepository;
import com.management.student_center.repository.SubjectRepository;
import com.management.student_center.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/materials/";

    public MaterialService(MaterialRepository materialRepository,
                           SubjectRepository subjectRepository,
                           UserRepository userRepository) {
        this.materialRepository = materialRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;

        // Tạo thư mục nếu chưa có
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
    }

    public List<MaterialDTO> getMaterialsBySubjectId(Long subjectId) {
        List<Material> materials = materialRepository.findBySubjectIdOrderByUploadedAtDesc(subjectId);

        return materials.stream().map(this::convertToDTO).collect(Collectors.toList());

    }

    public Material createMaterial(String title, Long subjectId, Long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Chưa có file tải lên");

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy môn học"));
        User user = userRepository.findById(userId).orElse(null);

        // Lưu file
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String fileName = System.currentTimeMillis() + "-" + (int)(Math.random()*1e9) + ext;
        Path path = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        Material material = new Material();
        material.setTitle(title);
        material.setFileURL("/uploads/materials/" + fileName);
        material.setType(ext.replace(".", "").toLowerCase());
        material.setUploadedAt(LocalDateTime.now());
        material.setSubject(subject);
        material.setUploadedBy(user);

        return materialRepository.save(material);
    }

    public Material updateMaterialFile(Long materialId, MultipartFile file, String newTitle) throws IOException {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài liệu"));

        if (file != null) {
            // Xóa file cũ
            Path oldPath = Paths.get(System.getProperty("user.dir") + material.getFileURL());
            Files.deleteIfExists(oldPath);

            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String fileName = System.currentTimeMillis() + "-" + (int)(Math.random()*1e9) + ext;
            Path path = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            material.setFileURL("/uploads/materials/" + fileName);
            material.setType(ext.replace(".", "").toLowerCase());
        }

        if (newTitle != null && !newTitle.isEmpty()) material.setTitle(newTitle);
        material.setUploadedAt(LocalDateTime.now());

        return materialRepository.save(material);
    }

    public void deleteMaterial(Long materialId) throws IOException {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài liệu"));

        if (material.getFileURL() != null) {
            Path path = Paths.get(System.getProperty("user.dir") + material.getFileURL());
            Files.deleteIfExists(path);
        }

        materialRepository.delete(material);
    }
    
    public MaterialDTO convertToDTO(Material m) {
        String fileSize = "Không xác định";
        try {
            Path path = Paths.get(System.getProperty("user.dir") + m.getFileURL());
            fileSize = String.format("%.2f MB", Files.size(path) / 1024.0 / 1024.0);
        } catch (IOException ignored) {}

        MaterialDTO dto = new MaterialDTO(
                m.getId(),
                m.getTitle(),
                m.getFileURL(),
                m.getType(),
                m.getUploadedAt(),
                m.getSubject() != null ? m.getSubject().getId() : null,
                m.getSubject() != null ? m.getSubject().getName() : null,
                m.getUploadedBy() != null ? m.getUploadedBy().getId() : null,
                m.getUploadedBy() != null ? m.getUploadedBy().getFullName() : null,
                fileSize
        );

        // Gán UserDTO
        if (m.getUploadedBy() != null) {
            MaterialDTO.UserDTO userDto = new MaterialDTO.UserDTO();
            userDto.setId(m.getUploadedBy().getId());
            userDto.setFullName(m.getUploadedBy().getFullName());
            userDto.setEmail(m.getUploadedBy().getEmail());
            dto.setUser(userDto);
        }

        return dto;
    }

}
