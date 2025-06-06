package com.brain.studio.microservice_orchestrator.service.module;

import com.brain.studio.microservice_orchestrator.model.MicroserviceDetails;
import com.brain.studio.microservice_orchestrator.service.module.aiService.RepositoryPromptService;
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
public class RepositoryService {

    @Value("${app.working-directory}")
    private String workingDirectory;

    private RepositoryPromptService repositoryPromptService;

    public RepositoryService(RepositoryPromptService repositoryPromptService){
        this.repositoryPromptService=repositoryPromptService;
    }

    public void createRepository(MicroserviceDetails microserviceDetails, String serviceClassCode, String projectName)throws IOException {
        String code=repositoryPromptService.getRepositoryCode(microserviceDetails,serviceClassCode);
        Map<String, String> placeholders = Map.of(
                "packageName", microserviceDetails.getGroupId(),
                "packageClass",microserviceDetails.getMicroserviceName(),
                "repository_code",code
        );
        System.out.println("Repository class response : "+code);
        ClassPathResource resource = new ClassPathResource("templates/RepositoryTemplate.java");
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
                "repository");

        Files.createDirectories(targetDir); // Ensure directory exists

        // Define target file path
        Path targetPath = targetDir.resolve(microserviceDetails.getEntity().getEntityName() + "Repository.java");

        // Write processed content to the new repository class
        Files.writeString(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Repository Class created at: " + targetPath);
    }
}
