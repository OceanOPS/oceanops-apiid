package org.oceanops.api.id;

import java.time.LocalDateTime;

/**
 * Basic bean representing the input values, auto filled with the JSON body of the POST request.
 */
public class IdInput {
    private String gtsId = null;
    private String program = null;
    private String model = null;
    private String batchStatus = null;
    private Double longitude = null;
    private Double latitude = null;
    private String serialNo = null;
    private String name = null;
    private String internalId = null;
    private LocalDateTime startDate = null;
    private LocalDateTime endDate = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNo() {
        return this.serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getGtsId() {
        return this.gtsId;
    }

    public void setGtsId(String gtsId) {
        this.gtsId = gtsId;
    }

    public String getProgram() {
        return this.program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBatchStatus() {
        return this.batchStatus;
    }

    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getInternalId() {
        return this.internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
