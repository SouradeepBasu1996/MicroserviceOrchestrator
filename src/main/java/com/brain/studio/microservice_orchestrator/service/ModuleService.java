package com.brain.studio.microservice_orchestrator.service;

import com.brain.studio.microservice_orchestrator.model.MicroserviceDetails;
import com.brain.studio.microservice_orchestrator.model.ProjectDetails;
import com.brain.studio.microservice_orchestrator.service.module.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ModuleService {

    private PomService pomService;
    private ApplicationService applicationService;
    private EntityService entityService;
    private ControllerService controllerService;
    private WebClientConfigService webClientConfigService;
    private PropertiesService propertiesService;

    public ModuleService(PomService pomService,
                         ApplicationService applicationService,
                         EntityService entityService,
                         ControllerService controllerService,
                         WebClientConfigService webClientConfigService,
                         PropertiesService propertiesService){
        this.pomService = pomService;
        this.applicationService=applicationService;
        this.entityService=entityService;
        this.controllerService=controllerService;
        this.webClientConfigService=webClientConfigService;
        this.propertiesService=propertiesService;
    }

    public void createModuleStructure(Path parentDir, MicroserviceDetails microserviceDetails, ProjectDetails projectDetails) throws IOException {

        Path serviceDir = parentDir.resolve(microserviceDetails.getMicroserviceName());

        // Create base service directory
        Files.createDirectories(serviceDir);

        // Create src directories
        Path mainJava = serviceDir.resolve("src/main/java");
        Path testJava = serviceDir.resolve("src/test/java");

        Files.createDirectories(mainJava);
        Files.createDirectories(testJava);

        //Generate Application Main class
        applicationService.createMainClass(microserviceDetails,projectDetails.getProjectName());

        // Generate submodule pom.xml
        pomService.generatePom(microserviceDetails,projectDetails);

        //Generate properties file
        propertiesService.createPropertiesFile(microserviceDetails,projectDetails.getProjectName());

        //Generate Entity classes
        entityService.createEntityClass(microserviceDetails,projectDetails.getProjectName());

        //Generate Controller class
        controllerService.createControllerClass(microserviceDetails,projectDetails.getProjectName());

        //Generate WebClient Config class
        //webClientConfigService.createWebConfig(microserviceDetails,projectDetails.getProjectName());

    }
}
