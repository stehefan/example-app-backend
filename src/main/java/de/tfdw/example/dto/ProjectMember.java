package de.tfdw.example.dto;


import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
