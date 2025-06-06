package com.brain.studio.microservice_orchestrator.service.module.aiService;

import com.brain.studio.microservice_orchestrator.model.ControllerMethod;
import com.brain.studio.microservice_orchestrator.model.ControllerMethodArgument;
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
public class ControllerPromptService {

    private final RestTemplate restTemplate;
    private final String FASTAPI_URL = "http://localhost:8000/generate";

    public ControllerPromptService(RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }

    public String generateControllerCode(MicroserviceDetails microserviceDetails){
        StringBuilder prompt = getPrompt(microserviceDetails);
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
                System.out.println("Controller Response code : "+code);
                String startTag = "<java>";
                String endTag = "</java>";

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
    private StringBuilder getPrompt(MicroserviceDetails microserviceDetails){
        StringBuilder prompt = new StringBuilder();

        prompt.append("Please respond with code only. No explanation or other String lines should be present.")
                .append("Create a Rest Controller class for java spring boot with name- ")
                .append(microserviceDetails.getControllerModel().getControllerClassName())
                .append(" and api endpoint- ")
                .append(microserviceDetails.getControllerModel().getRestControllerEndpoint())
                .append(". Inside the Controller class create the following methods. ");
        for(ControllerMethod method:microserviceDetails.getControllerModel().getControllerMethods()){
            prompt.append("Create method- ")
                    .append(method.getMethodName())
                    .append(" with arguments, ");
            for(ControllerMethodArgument argument:method.getMethodArguments()){
                prompt.append(argument)
                        .append("(")
                        .append(argument.getDataType())
                        .append(")")
                        .append(" and parameter annotation, ")
                        .append(argument.getParameterAnnotation())
                        .append(". ");
            }
            prompt.append("The method should return a ResponseEntity of suitable type depending on the return type ")
                    .append(method.getReturnType())
                    .append(" and http method- ")
                    .append(method.getHttpMethod())
                    .append(". Please put proper annotations over the method and arguments. The endpoint of the method is -")
                    .append(method.getEndPoint())
                    .append(". Inside the method, write the code to achieve the functionality of the method as given in the method description as- ")
                    .append(method.getDescription())
                    .append(". Use the reference of the Service class and model class names to write the code inside the method.")
                    .append("Service class name- ").append(microserviceDetails.getServiceClassName())
                    .append(". Model class names- ").append(microserviceDetails.getEntity().getEntityName())
                    .append(" having fields- ");
            for(EntityFieldModel entityField: microserviceDetails.getEntity().getEntityFields()){
                prompt.append(entityField.getFieldName())
                        .append("(").append(entityField.getDataType()).append("), ");
            }
            prompt.append("Assume that the Service classes and Entity classes are already created and hence do not need to create them again. ");
        }
        prompt.append("Use proper Spring Boot annotations and dependency injections in the controller and the methods. ")
                .append("In the response, enclose the java code part within <java>,</java> tags");


        System.out.println("Controller Prompt : "+prompt.toString());

        return prompt;
    }
}
