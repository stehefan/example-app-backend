package de.tfdw.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "project_member")
public class ProjectMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


}
