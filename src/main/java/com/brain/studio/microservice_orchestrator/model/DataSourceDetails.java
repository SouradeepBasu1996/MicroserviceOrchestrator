package com.brain.studio.microservice_orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataSourceDetails {

    private String portNumber;
    private String databaseName;
    private String userName;
    private String password;

}
