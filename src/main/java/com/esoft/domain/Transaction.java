package com.esoft.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "action")
    private String action;

    @Column(name = "status")
    private Integer status;

    @Column(name = "message")
    private String message;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "request_path")
    private String requestPath;

    @Column(name = "request_method")
    private String requestMethod;

    @Column(name = "username")
    private String username;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "token_history_id")
    private Long tokenHistoryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public Transaction action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Transaction status(Integer status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public Transaction message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeviceInfo() {
        return this.deviceInfo;
    }

    public Transaction deviceInfo(String deviceInfo) {
        this.setDeviceInfo(deviceInfo);
        return this;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Transaction createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getClientIp() {
        return this.clientIp;
    }

    public Transaction clientIp(String clientIp) {
        this.setClientIp(clientIp);
        return this;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getRequestPath() {
        return this.requestPath;
    }

    public Transaction requestPath(String requestPath) {
        this.setRequestPath(requestPath);
        return this;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public Transaction requestMethod(String requestMethod) {
        this.setRequestMethod(requestMethod);
        return this;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getUsername() {
        return this.username;
    }

    public Transaction username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Transaction userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDuration() {
        return this.duration;
    }

    public Transaction duration(Long duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getTokenHistoryId() {
        return this.tokenHistoryId;
    }

    public Transaction tokenHistoryId(Long tokenHistoryId) {
        this.setTokenHistoryId(tokenHistoryId);
        return this;
    }

    public void setTokenHistoryId(Long tokenHistoryId) {
        this.tokenHistoryId = tokenHistoryId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return getId() != null && getId().equals(((Transaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", status=" + getStatus() +
            ", message='" + getMessage() + "'" +
            ", deviceInfo='" + getDeviceInfo() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", clientIp='" + getClientIp() + "'" +
            ", requestPath='" + getRequestPath() + "'" +
            ", requestMethod='" + getRequestMethod() + "'" +
            ", username='" + getUsername() + "'" +
            ", userId=" + getUserId() +
            ", duration=" + getDuration() +
            ", tokenHistoryId=" + getTokenHistoryId() +
            "}";
    }
}
