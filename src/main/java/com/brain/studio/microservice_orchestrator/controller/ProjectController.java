package com.brain.studio.microservice_orchestrator.controller;

import com.brain.studio.microservice_orchestrator.model.ProjectDetails;
import com.brain.studio.microservice_orchestrator.service.ProjectService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/generate")
public class ProjectController {

    private ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService=projectService;
    }

    @PostMapping("/project")
    public ResponseEntity<Resource> generateProject(@RequestBody ProjectDetails projectDetails)throws IOException {
        Path zipFilePath = projectService.createProject(projectDetails);

        if (zipFilePath == null) {
            return ResponseEntity.internalServerError().build();
        }

        Resource resource = new UrlResource(zipFilePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFilePath.getFileName() + "\"")
                .body(resource);
    }
}
