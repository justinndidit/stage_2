package com.surgee.stage_2.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="_user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    private String userId;
    @NotBlank(message="First Name cannot be empty")
    private String firstName;

    @NotBlank(message="Last Name cannot be empty!")
    private String lastName;

    @NotBlank(message="Email cannot be empty")
    @Email(message="Please enter a valid Email address")
    private String email;

    private String phone;

    @NotBlank(message="Password cannot be blank")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
      name = "user_organization", 
      joinColumns = @JoinColumn(name = "user_id"), 
      inverseJoinColumns = @JoinColumn(name = "org_id"))
      @Builder.Default
    private List<Organization> organizations = new ArrayList<>();

    public void addOrganization(Organization organization) {
        this.organizations.add(organization);
        // organization.getUsers().add(this);
    }

	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

	@Override
	public String getUsername() {
		return getEmail();
	}
}
