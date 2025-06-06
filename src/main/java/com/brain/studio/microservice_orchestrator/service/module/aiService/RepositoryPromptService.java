package com.brain.studio.microservice_orchestrator.service.module.aiService;

import com.brain.studio.microservice_orchestrator.model.EntityFieldModel;
import com.brain.studio.microservice_orchestrator.model.MicroserviceDetails;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RepositoryPromptService {

    private final RestTemplate restTemplate;
    private final String FASTAPI_URL = "http://localhost:8000/generate";

    public RepositoryPromptService(RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }

    public String getRepositoryCode(MicroserviceDetails microserviceDetails, String serviceClassCode){
        StringBuilder prompt = getPrompt(microserviceDetails,serviceClassCode);
        String extractedCode="";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(FASTAPI_URL, requestEntity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("response")) {
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("response");

            if (responseBody.containsKey("content")) {
                String code = responseBody.get("content").toString();
                code = code.replaceAll("^```\\w*\\n?", "").replaceAll("\\n```$", "");
                String startTag = "<java>";
                String endTag = "</java>";

                int startIndex = code.indexOf(startTag);
                int endIndex = code.indexOf(endTag);

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    extractedCode = code.substring(startIndex + startTag.length(), endIndex).trim();
                    System.out.println(extractedCode);
                } else {
                    System.out.println("No Java code found!");
                }
                return extractedCode.trim();
            }
        }
        return "Failed to get response";
    }

    private StringBuilder getPrompt(MicroserviceDetails microserviceDetails, String serviceClassCode){
        StringBuilder prompt = new StringBuilder();

        prompt.append("Respond with code only, no explanation required.")
                .append(" Create an Interface for repository class in Spring Boot Application.")
                .append(" The interface extends JpaRepository")
                .append(" The repository interface is for the service class- ")
                .append("\n")
                .append(serviceClassCode)
                .append("\n")
                .append("Also for reference, here is the entity class- ")
                .append(microserviceDetails.getEntity().getEntityName())
                .append("having fields- ");
        for(EntityFieldModel entity:microserviceDetails.getEntity().getEntityFields()){
            prompt.append(entity.getFieldName())
                    .append("(").append(entity.getDataType()).append("), ");
        }
        prompt.append("Assume that the service class and the model classes already exists and do not need to be re created.")
                .append("Enclose the java code with in <java>,</java> tags");

        System.out.println(" Repository Prompt "+prompt.toString());

        return prompt;
    }
}
