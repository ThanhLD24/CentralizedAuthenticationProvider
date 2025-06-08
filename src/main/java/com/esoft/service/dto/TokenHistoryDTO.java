package com.esoft.service.dto;

import com.esoft.domain.enumeration.TokenStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.esoft.domain.TokenHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TokenHistoryDTO implements Serializable {

    private Long id;

    private String hashedToken;

    private Instant createdDate;

    private Instant updatedDate;

    private TokenStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHashedToken() {
        return hashedToken;
    }

    public void setHashedToken(String hashedToken) {
        this.hashedToken = hashedToken;
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

    public TokenStatus getStatus() {
        return status;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TokenHistoryDTO)) {
            return false;
        }

        TokenHistoryDTO tokenHistoryDTO = (TokenHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tokenHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TokenHistoryDTO{" +
            "id=" + getId() +
            ", hashedToken='" + getHashedToken() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
