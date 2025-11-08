package com.syscho.ai.chat.structured_response.model;

import lombok.Data;

import java.util.List;

@Data
public class HospitalInfoResponse {
    private String patientName;
    private String query;
    private String responseMessage;

    private List<ServiceInfo> services;

    @Data
    public static class ServiceInfo {
        private String type;
        private String name;
        private String description;
        private String cost;
        private String availability;
    }
}
