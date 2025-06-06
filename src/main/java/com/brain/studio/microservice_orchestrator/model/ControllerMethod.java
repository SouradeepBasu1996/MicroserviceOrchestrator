package com.brain.studio.microservice_orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControllerMethod {

    private String methodName;
    private String returnType;
    private List<ControllerMethodArgument> methodArguments;
    private String httpMethod;
    private String endPoint;
    private String description;
}
