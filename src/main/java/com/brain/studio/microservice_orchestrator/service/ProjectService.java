package com.brain.studio.microservice_orchestrator.service;

import com.brain.studio.microservice_orchestrator.model.MicroserviceDetails;
import com.brain.studio.microservice_orchestrator.model.ProjectDetails;
import com.brain.studio.microservice_orchestrator.service.service_discovery.DiscoveryServerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Scanner;

@Service
public class ProjectService {

    @Value("${app.working-directory}")
    private String workingDirectory;

    private final ZipService zipService;
    private final ModuleService moduleService;
    private final ParentPomService parentPomService;
    private final DiscoveryServerService discoveryServerService;

    public ProjectService(ZipService zipService,
                          ModuleService moduleService,
                          ParentPomService parentPomService,
                          DiscoveryServerService discoveryServerService){
        this.zipService= zipService;
        this.moduleService= moduleService;
        this.parentPomService=parentPomService;
        this.discoveryServerService=discoveryServerService;
    }

    public Path createProject(ProjectDetails projectDetails)throws IOException {
        propagateGroupId(projectDetails);
        Path projectDir = getProjectRootPath(projectDetails.getProjectName());

        Files.createDirectories(projectDir);
        createStructure(projectDetails);
        return zipService.zipProject(projectDir);
    }

    public void createStructure(ProjectDetails projectDetails)throws IOException {
        Path projectDir = getProjectRootPath(projectDetails.getProjectName());

        parentPomService.createParentPom(projectDetails);
        discoveryServerService.createDiscoveryServer(projectDir,projectDetails);

        for(MicroserviceDetails microserviceDetails:projectDetails.getMicroserviceDetailsList()){
            moduleService.createModuleStructure(projectDir,microserviceDetails,projectDetails);
        }
        createFileIfNotExists(projectDir.resolve("pom.xml"));
    }

    private Path getProjectRootPath(String projectName) {
        return Paths.get(workingDirectory, projectName);
    }
    private void createFileIfNotExists(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }
    private void propagateGroupId(ProjectDetails projectDetails) {
        String projectGroupId = projectDetails.getGroupId();
        for (MicroserviceDetails ms : projectDetails.getMicroserviceDetailsList()) {
            ms.setGroupId(projectGroupId);
        }
    }
}
