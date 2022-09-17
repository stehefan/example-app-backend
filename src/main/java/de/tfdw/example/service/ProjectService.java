package de.tfdw.example.service;

import de.tfdw.example.dto.Project;
import de.tfdw.example.dto.ProjectMapper;
import de.tfdw.example.model.ProjectEntity;
import de.tfdw.example.repository.ProjectEntityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProjectService {

    private final ProjectEntityRepository projectEntityRepository;

    private final ProjectMapper projectMapper;

    public ProjectService(final ProjectEntityRepository projectEntityRepository, final ProjectMapper projectMapper) {
        this.projectEntityRepository = projectEntityRepository;
        this.projectMapper = projectMapper;
    }

    public List<Project> listProjects() {
        return stream(projectEntityRepository.findAll().spliterator(), true)
                .map(projectMapper::mapFromProjectEntity)
                .collect(toList());
    }

    public Project getProject(final UUID projectID) {
        final ProjectEntity projectEntity = projectEntityRepository
                .findById(projectID)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(NOT_FOUND);
                });

        return projectMapper.mapFromProjectEntity(projectEntity);
    }

    public Project createProject(final Project project) {
        final ProjectEntity entity = new ProjectEntity(
                project.name(),
                project.description(),
                project.projectMembers()
        );
        return projectMapper.mapFromProjectEntity(projectEntityRepository.save(entity));
    }

    public Project updateProject(final Project project) {
        if (!projectEntityRepository.existsById(project.projectId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        final ProjectEntity entity = new ProjectEntity(
                project.projectId(),
                project.name(),
                project.description(),
                project.projectMembers()
        );
        return projectMapper.mapFromProjectEntity(projectEntityRepository.save(entity));
    }

    public Project deleteProject(final UUID projectID) {
        final ProjectEntity projectEntity = projectEntityRepository
                .findById(projectID)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(NOT_FOUND);
                });

        projectEntityRepository.delete(projectEntity);

        return projectMapper.mapFromProjectEntity(projectEntity);

    }
}
