package de.tfdw.example.dto;

import de.tfdw.example.model.ProjectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectMapperTest {

    private ProjectMapper projectMapper;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper();
    }

    @Test
    void shouldMapProjectEntity() {
        // given
        final UUID projectId = UUID.fromString("093e047e-b4ab-4053-87db-46c795426191");
        final ProjectEntity projectEntity = new ProjectEntity(
                projectId,
                "Test Project",
                "Test Description",
                Collections.emptyList()
        );
        final Project expectedProject = new Project(
                projectId,
                "Test Project",
                "Test Description",
                Collections.emptyList()
        );

        // when
        final Project actualProject = projectMapper.mapFromProjectEntity(projectEntity);

        // then
        assertEquals(expectedProject, actualProject);
    }

    @Test
    void shouldMapProjectEntityWithoutProjectId() {
        // given
        final ProjectEntity projectEntity = new ProjectEntity(
                "Test Project",
                "Test Description",
                Collections.emptyList()
        );
        final Project expectedProject = new Project(
                null,
                "Test Project",
                "Test Description",
                Collections.emptyList()
        );

        // when
        final Project actualProject = projectMapper.mapFromProjectEntity(projectEntity);

        // then
        assertEquals(expectedProject, actualProject);
    }

    @Test
    void shouldThrowExceptionIfProjectEntityIsNull() {
        // given / when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> projectMapper.mapFromProjectEntity(null),
                "Project Entity is required for mapping"
        );
    }
}
