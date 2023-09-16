package de.tfdw.example.dto;


import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Validated
public record Project(
        UUID projectId,

        @Size(min = 2, max = 256)
        String name,

        @Size(min = 10, max = 256)
        String description,

        List<ProjectMember> projectMembers
) {
}
