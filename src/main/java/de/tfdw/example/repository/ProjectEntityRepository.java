package de.tfdw.example.repository;

import de.tfdw.example.model.ProjectEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectEntityRepository extends CrudRepository<ProjectEntity, UUID> {
}
