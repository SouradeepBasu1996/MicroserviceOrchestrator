package com.brain.studio.microservice_orchestrator.service.module.aiService;

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
public class ServiceClassPromptService {

    private final RestTemplate restTemplate;
    private final String FASTAPI_URL = "http://localhost:8000/generate";

    public ServiceClassPromptService(RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }

    public String getServiceClassCode(MicroserviceDetails microserviceDetails, String controllerClassCode){
        StringBuilder prompt = getPrompt(microserviceDetails,controllerClassCode);
        System.out.println("Service Class Prompt : \n"+prompt);
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
                System.out.println("Service class Code : "+code);
                int startIndex = code.indexOf(startTag);
                int endIndex = code.indexOf(endTag);

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    extractedCode = code.substring(startIndex + startTag.length(), endIndex).trim();
                } else {
                    System.out.println("No Java code found!");
                }
                return extractedCode;
            }
        }
        return "Failed to get response";
    }
    private StringBuilder getPrompt(MicroserviceDetails microserviceDetails, String controllerClassCode){
        StringBuilder prompt = new StringBuilder();

        if(microserviceDetails.getDependentOn().equalsIgnoreCase("none"))
            prompt.append("Respond with code only, no explanation required.")
                    .append(" Create a Service class for a Spring Boot Application, with name- ")
                    .append(microserviceDetails.getServiceClassName())
                    .append("\n The methods inside the service class should be the written according to the requirements of the controller class code as given below- ")
                    .append("\n")
                    .append(controllerClassCode)
                    .append("\n")
                    .append("Assume all other classes already exists hence do not need to create them.")
                    .append("Enclose the java code with in <java>,</java> tags");
        else
            prompt.append("Respond with code only, no explanation required.")
                    .append(" Create a Service class for a Spring Boot Application, with name- ")
                    .append(microserviceDetails.getServiceClassName())
                    .append("\n The methods inside the service class should be the written according to the requirements of the controller class code as given below- \n")
                    .append("\n")
                    .append(controllerClassCode)
                    .append("\n")
                    .append("And one or more methods are dependent on another microservice : ")
                    .append(microserviceDetails.getDependentOn())
                    .append(" So connect this two microservices using webclient in appropriate methods, where the webclient configuration is given as ")
                    .append("@Configuration\n")
                    .append("public class WebClientConfig {\n")
                    .append("    @Bean\n")
                    .append("    @LoadBalanced\n")
                    .append("    public WebClient.Builder webClientBuilder() {\n")
                    .append("        return WebClient.builder();\n")
                    .append("    }\n")
                    .append("}")
                    .append("Assume all other classes already exists hence do not need to create them.")
                    .append("Enclose the java code with in <java>,</java> tags");

        System.out.println("Service class prompt : "+prompt.toString());

        return prompt;
    }
}
