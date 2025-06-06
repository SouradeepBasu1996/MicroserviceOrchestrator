package com.brain.studio.microservice_orchestrator.service.service_discovery;

import com.brain.studio.microservice_orchestrator.model.MicroserviceDetails;
import com.brain.studio.microservice_orchestrator.model.ProjectDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

@Service
public class DSPropertiesService {

    @Value("${app.working-directory}")
    private String workingDirectory;

    public void createPropertiesFile(ProjectDetails projectDetails)throws IOException {

        // Read the template file from classpath
        ClassPathResource resource = new ClassPathResource("templates/DSProperties.properties");
        String content;
        try (InputStream inputStream = resource.getInputStream(); Scanner scanner = new Scanner(inputStream)) {
            content = scanner.useDelimiter("\\A").next(); // Read entire file as a string
        }

        Path targetDir = Path.of(workingDirectory,
                projectDetails.getProjectName(),
                "discoveryserver",
                "src/main/resources");

        Files.createDirectories(targetDir); // Ensure directory exists

        // Define target file path
        Path targetPath = targetDir.resolve("application.properties");

        // Write processed content to the new main class
        Files.writeString(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Entity Class created at: " + targetPath);
    }
}
