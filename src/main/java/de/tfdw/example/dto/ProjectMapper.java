package de.tfdw.example.dto;

import de.tfdw.example.model.ProjectEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ProjectMapper {

    public Project mapFromProjectEntity(final ProjectEntity project) {
        Assert.notNull(project, "Project Entity is required for mapping");

        return new Project(
                project.id,
                project.name,
                project.description,
                project.projectMembers
        );
    }
}
