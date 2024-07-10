package com.surgee.stage_2.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    @Id
    private String orgId;
    private String name;
    private String description;

    @ManyToMany(mappedBy = "organizations")
    @JsonIgnore
    @Builder.Default
    private List<User> users = new ArrayList<>();
}
