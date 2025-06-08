package com.esoft.domain;

import com.esoft.domain.enumeration.TokenStatus;
import com.esoft.domain.enumeration.TokenType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TokenHistory.
 */
@Entity
@Table(name = "token_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TokenHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "hashed_token")
    private String hashedToken;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "updated_date")
    private Instant updatedDate;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TokenStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TokenType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Long getId() {
        return this.id;
    }

    public TokenHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHashedToken() {
        return this.hashedToken;
    }

    public TokenHistory hashedToken(String hashedToken) {
        this.setHashedToken(hashedToken);
        return this;
    }

    public void setHashedToken(String hashedToken) {
        this.hashedToken = hashedToken;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public TokenHistory createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return this.updatedDate;
    }

    public TokenHistory updatedDate(Instant updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public TokenStatus getStatus() {
        return this.status;
    }

    public TokenHistory status(TokenStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public Instant getExpiryDate() {
        return this.expiryDate;
    }
    public TokenHistory expiryDate(Instant expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public TokenType getType() {
        return this.type;
    }

    public TokenHistory type(TokenType type) {
        this.setType(type);
        return this;
    }
    public void setType(TokenType type) {
        this.type = type;
    }
    public User getUser() {
        return this.user;
    }
    public TokenHistory user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TokenHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((TokenHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TokenHistory{" +
            "id=" + getId() +
            ", hashedToken='" + getHashedToken() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
