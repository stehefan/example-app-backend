package de.tfdw.example.service;

import de.tfdw.example.dto.Project;
import de.tfdw.example.dto.ProjectMapper;
import de.tfdw.example.model.ProjectEntity;
import de.tfdw.example.repository.ProjectEntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ProjectServiceTest {

    private static final String STATIC_UUID = "093e047e-b4ab-4053-87db-46c795426191";

    private AutoCloseable autoCloseable;
    @Mock
    private ProjectEntityRepository mockedProjectEntityRepository;
    @Mock
    private ProjectMapper mockedProjectMapper;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        autoCloseable = openMocks(this);
        projectService = new ProjectService(mockedProjectEntityRepository, mockedProjectMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldCallRepositoryOnList() {
        // given
        final ProjectEntity projectEntity = mock(ProjectEntity.class);
        final Project project = mock(Project.class);

        when(mockedProjectEntityRepository.findAll()).thenReturn(singletonList(projectEntity));
        when(mockedProjectMapper.mapFromProjectEntity(projectEntity)).thenReturn(project);

        // when
        final List<Project> retrievedProject = projectService.listProjects();

        // then
        assertArrayEquals(singletonList(project).toArray(), retrievedProject.toArray());
        verify(mockedProjectEntityRepository).findAll();
        verify(mockedProjectMapper).mapFromProjectEntity(projectEntity);
    }

    @Test
    void shouldReturnEmptyListIfNoneAreAvailable() {
        // given
        when(mockedProjectEntityRepository.findAll()).thenReturn(emptyList());

        // when
        final List<Project> retrievedProject = projectService.listProjects();

        // then
        assertEquals(0, retrievedProject.size());
        verify(mockedProjectEntityRepository).findAll();
        verifyNoInteractions(mockedProjectMapper);
    }

    @Test
    void shouldCallRepositoryOnGet() {
        // given
        final UUID projectId = UUID.fromString(STATIC_UUID);
        final ProjectEntity projectEntity = mock(ProjectEntity.class);
        final Project project = mock(Project.class);

        when(mockedProjectEntityRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
        when(mockedProjectMapper.mapFromProjectEntity(projectEntity)).thenReturn(project);

        // when
        final Project retrievedProject = projectService.getProject(projectId);

        // then
        assertEquals(project, retrievedProject);
        verify(mockedProjectEntityRepository).findById(projectId);
        verify(mockedProjectMapper).mapFromProjectEntity(projectEntity);
    }

    @Test
    void shouldThrowExceptionIfProjectIdIsNotKnown() {
        // given
        final UUID projectId = UUID.fromString(STATIC_UUID);

        when(mockedProjectEntityRepository.findById(projectId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResponseStatusException.class, () -> projectService.getProject(projectId), "Not Found");
        verify(mockedProjectEntityRepository).findById(projectId);
        verifyNoInteractions(mockedProjectMapper);
    }

    @Test
    void shouldCreateProject() {
        // given
        final UUID projectId = UUID.fromString(STATIC_UUID);
        final Project functionPayload = new Project(
                null,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final ProjectEntity expectedEntityToSave = new ProjectEntity(
                null,
                "Test Name",
                "Test Description",
                emptyList()
        );

        final Project returnProject = new Project(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final ProjectEntity retrievedEntity = new ProjectEntity(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );

        when(mockedProjectEntityRepository.save(expectedEntityToSave)).thenReturn(retrievedEntity);
        when(mockedProjectMapper.mapFromProjectEntity(retrievedEntity)).thenReturn(returnProject);

        // when
        final Project actualProject = projectService.createProject(functionPayload);

        // then
        assertEquals(returnProject, actualProject);
        verify(mockedProjectEntityRepository).save(expectedEntityToSave);
        verify(mockedProjectMapper).mapFromProjectEntity(retrievedEntity);
    }

    @Test
    void shouldUpdateProject() {
        // given
        final UUID projectId = UUID.fromString(STATIC_UUID);
        final Project functionPayload = new Project(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final ProjectEntity expectedEntityToSave = new ProjectEntity(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );

        final Project returnProject = new Project(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final ProjectEntity retrievedEntity = new ProjectEntity(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );

        when(mockedProjectEntityRepository.save(expectedEntityToSave)).thenReturn(retrievedEntity);
        when(mockedProjectMapper.mapFromProjectEntity(retrievedEntity)).thenReturn(returnProject);
        when(mockedProjectEntityRepository.existsById(projectId)).thenReturn(true);

        // when
        final Project actualProject = projectService.updateProject(functionPayload);

        // then
        assertEquals(returnProject, actualProject);
        verify(mockedProjectEntityRepository).save(expectedEntityToSave);
        verify(mockedProjectMapper).mapFromProjectEntity(retrievedEntity);
    }

    @Test
    void shouldCheckIfProjectExistsOnUpdate() {
        // given
        final UUID projectId = UUID.fromString(STATIC_UUID);
        final Project functionPayload = new Project(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );
        when(mockedProjectEntityRepository.existsById(projectId)).thenReturn(false);

        // when / then
        assertThrows(ResponseStatusException.class, () -> projectService.updateProject(functionPayload));
        verify(mockedProjectEntityRepository).existsById(projectId);
    }

    @Test
    void shouldDeleteProject() {
        // given
        final UUID projectId = UUID.fromString(STATIC_UUID);
        final Project expectedProject = new Project(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final Project mappedProject = new Project(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final ProjectEntity projectEntity = new ProjectEntity(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );

        when(mockedProjectEntityRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
        when(mockedProjectMapper.mapFromProjectEntity(projectEntity)).thenReturn(mappedProject);

        // when
        final Project actualProject = projectService.deleteProject(projectId);

        // then
        assertEquals(expectedProject, actualProject);
        verify(mockedProjectEntityRepository).findById(projectId);
        verify(mockedProjectEntityRepository).delete(projectEntity);
        verify(mockedProjectMapper).mapFromProjectEntity(projectEntity);
    }

    @Test
    void shouldthrowNotFoundExceptionWhenProjectToBeDeletedIsNotInRepo() {
        // given
        final UUID projectId = UUID.fromString(STATIC_UUID);

        when(mockedProjectEntityRepository.findById(projectId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(
                ResponseStatusException.class,
                () -> projectService.deleteProject(projectId),
                "Not Found"
        );
        verify(mockedProjectEntityRepository).findById(projectId);
        verifyNoMoreInteractions(mockedProjectEntityRepository);
        verifyNoMoreInteractions(mockedProjectMapper);
    }
}
