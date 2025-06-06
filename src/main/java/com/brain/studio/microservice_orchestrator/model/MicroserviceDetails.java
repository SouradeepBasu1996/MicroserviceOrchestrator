package com.brain.studio.microservice_orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MicroserviceDetails {

    private String microserviceName;
    private String groupId;
    private String artifactId;
    private String description;
    private ControllerModel controllerModel;
    private String serviceClassName;
    private EntityModel entity;
    private String portNumber;
    private DataSourceDetails dataSourceDetails;
    private String dependentOn;
}
