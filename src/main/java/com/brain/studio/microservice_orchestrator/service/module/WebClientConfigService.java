package com.brain.studio.microservice_orchestrator.service.module;

import com.brain.studio.microservice_orchestrator.model.MicroserviceDetails;
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
public class WebClientConfigService {

    @Value("${app.working-directory}")
    private String workingDirectory;

    public void createWebConfig(MicroserviceDetails microserviceDetails,String projectName)throws IOException {

        Map<String, String> placeholders = Map.of(
                "packageName",microserviceDetails.getGroupId(),
                "packageClass",microserviceDetails.getArtifactId()
        );

        ClassPathResource resource = new ClassPathResource("templates/WebClientConfig.java");
        String content;
        try (InputStream inputStream = resource.getInputStream(); Scanner scanner = new Scanner(inputStream)) {
            content = scanner.useDelimiter("\\A").next(); // Read entire file as a string
        }

        // Replace placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            content = content.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        String packagePath = microserviceDetails.getGroupId().replace(".", "/")
                +"/"
                +microserviceDetails.getMicroserviceName();

        Path targetDir = Path.of(workingDirectory,
                projectName,
                microserviceDetails.getMicroserviceName(),
                "src/main/java",
                packagePath,
                "config");

        Files.createDirectories(targetDir); // Ensure directory exists

        // Define target file path
        Path targetPath = targetDir.resolve(microserviceDetails.getServiceClassName()+ ".java");

        // Write processed content to the new service class
        Files.writeString(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("WebClient Config Class created at: " + targetPath);
    }
}
