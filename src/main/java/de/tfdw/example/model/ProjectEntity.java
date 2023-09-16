package de.tfdw.example.model;

import de.tfdw.example.dto.ProjectMember;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public UUID id;

    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    @ManyToMany(targetEntity = ProjectMemberEntity.class, fetch = FetchType.EAGER)
    public List<ProjectMember> projectMembers;

    public ProjectEntity() {
    }

    public ProjectEntity(final String name, final String description, final List<ProjectMember> projectMembers) {
        this.name = name;
        this.description = description;
        this.projectMembers = projectMembers;
    }

    public ProjectEntity(final UUID id, final String name, final String description, final List<ProjectMember> projectMembers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projectMembers = projectMembers;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<ProjectMember> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(final List<ProjectMember> projectMembers) {
        this.projectMembers = projectMembers;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProjectEntity that = (ProjectEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
