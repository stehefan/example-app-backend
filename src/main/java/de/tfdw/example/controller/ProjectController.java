package de.tfdw.example.controller;

import de.tfdw.example.dto.Project;
import de.tfdw.example.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Project> getProjects() {
        return projectService.listProjects();
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@Validated @RequestBody final Project project) {
        if (Objects.nonNull(project.projectId())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "ProjectID shall not be set when creating a project");
        }
        return projectService.createProject(project);
    }

    @PostMapping(value = "/{projectID}", produces = APPLICATION_JSON_VALUE)
    public Project updateProject(@PathVariable("projectID") final UUID projectID, @Validated @RequestBody final Project project) {
        if (!projectID.equals(project.projectId())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "ProjectIDs do not match");
        }
        return projectService.updateProject(project);
    }

    @GetMapping(value = "/{projectID}", produces = APPLICATION_JSON_VALUE)
    public Project getProject(@PathVariable("projectID") final UUID projectID) {
        return projectService.getProject(projectID);
    }

    @DeleteMapping(value = "/{projectID}", produces = APPLICATION_JSON_VALUE)
    public Project deleteProject(@PathVariable("projectID") final UUID projectID) {
        return projectService.deleteProject(projectID);
    }
}
