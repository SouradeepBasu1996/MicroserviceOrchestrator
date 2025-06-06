package com.brain.studio.microservice_orchestrator.service.service_discovery;

import com.brain.studio.microservice_orchestrator.model.ProjectDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DiscoveryServerService {

    private DSPomService pomService;
    private DSAppService DSAppService;
    private DSPropertiesService dsPropertiesService;

    public DiscoveryServerService(DSPomService pomService,
                                  DSAppService DSAppService,
                                  DSPropertiesService dsPropertiesService){
        this.pomService=pomService;
        this.DSAppService = DSAppService;
        this.dsPropertiesService=dsPropertiesService;
    }

    public void createDiscoveryServer(Path parentDir, ProjectDetails projectDetails)throws IOException {
        Path serviceDir = parentDir.resolve("discoveryserver");

        // Create base service directory
        Files.createDirectories(serviceDir);

        // Create src directories
        Path mainJava = serviceDir.resolve("src/main/java");
        Path mainResources = serviceDir.resolve("src/main/resources");

        Files.createDirectories(mainJava);
        Files.createDirectories(mainResources);

        pomService.generatePom(projectDetails);
        DSAppService.createMainClass(projectDetails);
        dsPropertiesService.createPropertiesFile(projectDetails);

    }
}
