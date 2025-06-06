package com.brain.studio.microservice_orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetails {

    private String projectName;
    private String groupId;
    private String artifactId;
    private List<MicroserviceDetails> microserviceDetailsList;
}
