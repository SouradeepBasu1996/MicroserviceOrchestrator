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
public class PropertiesService {

    @Value("${app.working-directory}")
    private String workingDirectory;

    public void createPropertiesFile(MicroserviceDetails microserviceDetails,String projectName)throws IOException {
        // Define placeholders
        Map<String, String> placeholders = Map.of(
                "microservice-name", microserviceDetails.getMicroserviceName(),
                "portNumber", microserviceDetails.getDataSourceDetails().getPortNumber(),
                "databaseName", microserviceDetails.getDataSourceDetails().getDatabaseName(),
                "userName", microserviceDetails.getDataSourceDetails().getUserName(),
                "password", microserviceDetails.getDataSourceDetails().getPassword()
        );

        // Read the template file from classpath
        ClassPathResource resource = new ClassPathResource("templates/propertiesTemplate.properties");
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
                "src/main/resources");

        Files.createDirectories(targetDir); // Ensure directory exists

        // Define target file path
        Path targetPath = targetDir.resolve("application.properties");

        // Write processed content to the new main class
        Files.writeString(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Entity Class created at: " + targetPath);
    }
}
