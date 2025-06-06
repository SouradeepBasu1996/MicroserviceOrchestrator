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
public class ControllerModel {

    private String controllerClassName;
    private String restControllerEndpoint;
    private List<ControllerMethod> controllerMethods;
}
