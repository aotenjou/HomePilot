package com.example.manager.DTO;

public class PlantCareRequest {
    private String input;
    private Long deviceId;

    public PlantCareRequest() {}

    public PlantCareRequest(String input) {
        this.input = input;
    }

    public PlantCareRequest(String input, Long deviceId) {
        this.input = input;
        this.deviceId = deviceId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
}
