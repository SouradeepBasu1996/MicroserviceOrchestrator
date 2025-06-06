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
import java.util.Map;
import java.util.Scanner;
@Service
public class DSPomService {

    @Value("${app.working-directory}")
    private String workingDirectory;

    public void generatePom(ProjectDetails projectDetails) throws IOException {
        // Define placeholders
        Map<String, String> placeholders = Map.of(
                "parentGroupId", projectDetails.getGroupId(),
                "parentArtifactId", projectDetails.getProjectName()
        );

        // Read the template file from classpath
        ClassPathResource resource = new ClassPathResource("templates/discovery-server-pom.xml");
        String content;
        try (InputStream inputStream = resource.getInputStream(); Scanner scanner = new Scanner(inputStream)) {
            content = scanner.useDelimiter("\\A").next(); // Read entire file as a string
        }

        // Replace placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            content = content.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        // Define target file path
        Path targetPath = Path.of(workingDirectory,projectDetails.getProjectName(), "discoveryserver", "pom.xml");

        // Ensure parent directory exists
        Files.createDirectories(targetPath.getParent());

        // Write processed content to the new POM file
        Files.writeString(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("POM file created at: " + targetPath);
    }
}
