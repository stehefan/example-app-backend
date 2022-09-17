package de.tfdw.example.integration;

import de.tfdw.example.dto.Project;
import de.tfdw.example.dto.ProjectMapper;
import de.tfdw.example.model.ProjectEntity;
import de.tfdw.example.repository.ProjectEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class ProjectControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProjectMapper projectMapper;

    @Container
    @SuppressWarnings("rawtypes")
    static final PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:14.5")
            .withDatabaseName("testDatabase")
            .withUsername("testUser")
            .withPassword("testPassword");


    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        postgresqlContainer.start();
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @BeforeEach
    void setUp() {
        projectEntityRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        postgresqlContainer.isRunning();

        assertEquals(0, projectEntityRepository.count());
    }

    @Test
    void returnEmptyListOfProjects() {
        // given

        // when
        final ResponseEntity<Project[]> responseEntity = testRestTemplate.getForEntity("/project", Project[].class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(responseEntity.getBody());
        assertEquals(0, responseEntity.getBody().length);
    }

    @Test
    void returnListOfProjects() {
        // given
        final ProjectEntity projectEntity = new ProjectEntity(
                null,
                "Test Project",
                "Test Description",
                emptyList()
        );
        projectEntityRepository.save(projectEntity);

        // when
        final ResponseEntity<Project[]> responseEntity = testRestTemplate.getForEntity("/project", Project[].class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().length);
    }

    @Test
    void createProject() {
        // given
        final Project projectToCreate = new Project(
                null,
                "Test Name",
                "Test Description",
                emptyList()
        );

        // when
        final ResponseEntity<Project> responseEntity = testRestTemplate.postForEntity(URI.create("/project"), projectToCreate, Project.class);

        // then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(responseEntity.getBody());
        assertTrue(matchProjectWithoutId(responseEntity.getBody(), projectToCreate));
    }

    @Test
    void createProjectFailsWhenIdIsGiven() {
        // given
        final Project projectToCreate = new Project(
                UUID.randomUUID(),
                "Test Name",
                "Test Description",
                emptyList()
        );

        // when
        final ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/project", projectToCreate, String.class);

        // then
        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("ProjectID shall not be set when creating a project"));

    }

    @Test
    void updateProject() {
        // given
        final ProjectEntity projectEntity = new ProjectEntity(
                null,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final ProjectEntity savedProjectEntity = projectEntityRepository.save(projectEntity);
        final Project projectToUpdate = projectMapper.mapFromProjectEntity(savedProjectEntity);

        // when
        final ResponseEntity<Project> responseEntity = testRestTemplate.postForEntity("/project/" + projectToUpdate.projectId(), projectToUpdate, Project.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void updateProjectFailsWhenIdsDontMatch() {
        // given
        final Project projectToUpdate = new Project(
                UUID.randomUUID(),
                "Test Name",
                "Test Description",
                emptyList()
        );

        // when
        final ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/project/" + UUID.randomUUID(), projectToUpdate, String.class);

        // then
        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("ProjectIDs do not match"));
    }

    @Test
    void updateProjectFailsWhenProjectDoesNotExist() {
        // given
        final UUID projectId = UUID.randomUUID();
        final Project projectToUpdate = new Project(
                projectId,
                "Test Name",
                "Test Description",
                emptyList()
        );

        // when
        final ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/project/" + projectId, projectToUpdate, String.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void getProject() {
        // given
        final ProjectEntity projectEntity = new ProjectEntity(
                null,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final ProjectEntity savedProjectEntity = projectEntityRepository.save(projectEntity);
        final Project savedProject = projectMapper.mapFromProjectEntity(savedProjectEntity);

        // when
        final ResponseEntity<Project> responseEntity = testRestTemplate.getForEntity("/project/" + savedProject.projectId(), Project.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void getProjectThrows404IfNotFound() {
        // given

        // when
        final ResponseEntity<Project> responseEntity = testRestTemplate.getForEntity("/project/" + UUID.randomUUID(), Project.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
    }

    @Test
    void deleteProject() {
        // given
        final ProjectEntity projectEntity = new ProjectEntity(
                null,
                "Test Name",
                "Test Description",
                emptyList()
        );
        final ProjectEntity savedProjectEntity = projectEntityRepository.save(projectEntity);
        final Project savedProject = projectMapper.mapFromProjectEntity(savedProjectEntity);
        final RequestEntity<Void> request = RequestEntity.method(HttpMethod.DELETE, "/project/" + savedProject.projectId()).build();

        // when
        final ResponseEntity<Project> responseEntity = testRestTemplate.exchange(request, Project.class);

        // then
        final Project body = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(body);
    }

    @Test
    void deleteProjectThrows404IfNotFound() {
        // given
        final RequestEntity<Void> request = RequestEntity.method(HttpMethod.DELETE, "/project/" + UUID.randomUUID()).build();

        // when
        final ResponseEntity<Project> responseEntity = testRestTemplate.exchange(request, Project.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(APPLICATION_JSON, responseEntity.getHeaders().getContentType());
    }

    private boolean matchProjectWithoutId(final Project a, final Project b) {
        final boolean equals = a.projectMembers().equals(b.projectMembers());
        return a.name().equals(b.name())
                && a.description().equals(b.description())
                && equals;
    }

}
