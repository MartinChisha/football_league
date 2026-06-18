package com.backspacestudios.league_management.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    // Base upload directory (configure in application.properties)
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final String TEAM_LOGO_DIR = "teams/logos";

    public String saveTeamLogo(MultipartFile file, UUID teamId) throws IOException {
        String fileName = "team_logo_" + teamId.toString() + "_" + System.currentTimeMillis() + ".jpg";
        return saveFile(file, TEAM_LOGO_DIR, fileName);
    }

    // Profile image sub‑folder
    private static final String PROFILE_DIR = "profiles";
    // Product image sub‑folder
    private static final String PRODUCT_DIR = "marketplace/products";

    public String saveProfileImage(MultipartFile file, UUID userId) throws IOException {
        String fileName = "profile_" + userId.toString() + "_" + System.currentTimeMillis() + ".jpg";
        String subDir = PROFILE_DIR;
        return saveFile(file, subDir, fileName);
    }

    public String saveProductImage(MultipartFile file, UUID productId, int order) throws IOException {
        String fileName = "product_" + productId.toString() + "_" + order + "_" + System.currentTimeMillis() + ".jpg";
        String subDir = PRODUCT_DIR;
        return saveFile(file, subDir, fileName);
    }

    private String saveFile(MultipartFile file, String subDir, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Return the URL (e.g., /uploads/profiles/filename.jpg)
        // Adjust base URL if you use CDN or absolute path
        return "/uploads/" + subDir + "/" + fileName;
    }
}