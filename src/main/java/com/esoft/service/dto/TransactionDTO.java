package com.esoft.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.esoft.domain.Transaction} entity.
 */
@Data
@NoArgsConstructor
public class TransactionDTO implements Serializable {

    private Long id;

    private String action;

    private Integer status;

    private String message;

    private String deviceInfo;

    private Instant createdDate;

    private String clientIp;

    private String requestPath;

    private String requestMethod;

    private String username;

    private Long userId;

    private Long duration;

    private Long tokenHistoryId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getTokenHistoryId() {
        return tokenHistoryId;
    }

    public void setTokenHistoryId(Long tokenHistoryId) {
        this.tokenHistoryId = tokenHistoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionDTO)) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionDTO{" +
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
