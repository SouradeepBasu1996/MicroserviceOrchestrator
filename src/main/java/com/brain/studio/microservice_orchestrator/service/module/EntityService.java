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
public class EntityService {

    @Value("${app.working-directory}")
    private String workingDirectory;

    public void createEntityClass(MicroserviceDetails microserviceDetails,String projectName)throws IOException {
        StringBuilder fieldsBuilder = new StringBuilder();
        microserviceDetails.getEntity().getEntityFields().forEach(entityModel -> {
            fieldsBuilder.append("    @Column(name =\"")
                    .append(formatSnakeCase(entityModel.getFieldName()))
                    .append("\")\n")
                    .append("    ")
                    .append("private ")
                    .append(entityModel.getDataType())
                    .append(" ")
                    .append(entityModel.getFieldName())
                    .append(";\n");
        });

        Map<String, String> placeholders = Map.of(
                "packageName",microserviceDetails.getGroupId(),
                "packageClass",microserviceDetails.getArtifactId(),
                "entityName",microserviceDetails.getEntity().getEntityName(),
                "tableName",formatSnakeCase(microserviceDetails.getEntity().getEntityName()),
                "fields",fieldsBuilder.toString()
        );

        ClassPathResource resource = new ClassPathResource("templates/EntityTemplate.java");
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
                "entity");

        Files.createDirectories(targetDir); // Ensure directory exists

        // Define target file path
        Path targetPath = targetDir.resolve(microserviceDetails.getEntity().getEntityName() + ".java");

        // Write processed content to the new main class
        Files.writeString(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Entity Class created at: " + targetPath);
    }

    private String formatSnakeCase(String fieldName){

        String str = fieldName.substring(0,1).toLowerCase()+fieldName.substring(1);

        StringBuilder tableName = new StringBuilder();

        for(int index = 0; index<str.length();index++){
            char hump =str.charAt(index);

            if(Character.isUpperCase(hump)){
                if(index>0)
                    tableName.append('_');
                tableName.append(Character.toLowerCase(hump));
            }
            else{
                tableName.append(hump);
            }
        }

        return tableName.toString();
    }
}
