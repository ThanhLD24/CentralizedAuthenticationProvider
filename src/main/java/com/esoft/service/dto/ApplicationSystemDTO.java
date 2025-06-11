package com.esoft.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.esoft.domain.ApplicationSystem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApplicationSystemDTO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private Instant createdDate;

    private Instant updatedDate;

    private Boolean active;

    private String hashedSecretKey;

    private Set<UserDTO> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getHashedSecretKey() {
        return hashedSecretKey;
    }

    public void setHashedSecretKey(String hashedSecretKey) {
        this.hashedSecretKey = hashedSecretKey;
    }

    public Set<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(Set<UserDTO> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationSystemDTO)) {
            return false;
        }

        ApplicationSystemDTO applicationSystemDTO = (ApplicationSystemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, applicationSystemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationSystemDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", active='" + getActive() + "'" +
            ", hashedSecretKey='" + getHashedSecretKey() + "'" +
            ", users=" + getUsers() +
            "}";
    }
}
