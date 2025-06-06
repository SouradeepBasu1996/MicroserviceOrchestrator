package com.brain.studio.microservice_orchestrator.service.service_discovery;

import com.brain.studio.microservice_orchestrator.model.ProjectDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Scanner;

@Service
public class DSAppService {

    @Value("${app.working-directory}")
    private String workingDirectory;

    public void createMainClass(ProjectDetails projectDetails)throws IOException {
        String className = "DiscoveryServerApplication";
        Map<String, String> placeholders = Map.of(
                "packageName", projectDetails.getGroupId());

        ClassPathResource resource = new ClassPathResource("templates/DSApplicationTemp.java");
        String content;
        try (InputStream inputStream = resource.getInputStream(); Scanner scanner = new Scanner(inputStream)) {
            content = scanner.useDelimiter("\\A").next(); // Read entire file as a string
        }

        // Replace placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            content = content.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        String packagePath = projectDetails.getGroupId().replace(".", "/")
                +"/discoveryserver";

        Path targetDir = Path.of(workingDirectory,
                projectDetails.getProjectName(),
                "discoveryserver/src/main/java",
                packagePath);

        Files.createDirectories(targetDir); // Ensure directory exists

        // Define target file path
        Path targetPath = targetDir.resolve(className + ".java");

        // Write processed content to the new main class
        Files.writeString(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Main Class created at: " + targetPath);
    }
}
