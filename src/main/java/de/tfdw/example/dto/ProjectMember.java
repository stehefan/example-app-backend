package de.tfdw.example.dto;


import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Validated
public record ProjectMember(
        UUID projectMemberId,

        @Size(min = 2, max = 255)
        String firstName,

        @Size(min = 2, max = 255)
        String lastName,

        @Email
        String email,

        @Pattern(regexp = "(he/him)|(she/her)|(they/their)")
        String pronouns,

        @Size(min = 10, max = 255)
        String description,

        @NotBlank
        String imageReference
) {
};
