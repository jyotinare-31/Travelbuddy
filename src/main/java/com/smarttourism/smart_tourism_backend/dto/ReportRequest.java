package com.smarttourism.smart_tourism_backend.dto;

public class ReportRequest {
    private String reason;

    public ReportRequest() {
    }

    public ReportRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

