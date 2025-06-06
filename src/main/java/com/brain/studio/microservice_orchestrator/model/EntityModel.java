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
public class EntityModel {

    private String entityName;
    private List<EntityFieldModel> entityFields;
}
