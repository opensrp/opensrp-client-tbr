package org.smartregister.tbr.model;

/**
 * Created by ndegwamartin on 26/01/2018.
 */

public class BMIRecord {
    private Long id;
    private String baseEntityId;
    private Float height;
    private Float weight;
    private Float bmi;
    private String treatmentInitiationDate;
    private String createdAt;
    private Long updatedAt;

    public String getTreatmentInitiationDate() {
        return treatmentInitiationDate;
    }

    public void setTreatmentInitiationDate(String treatmentInitiationDate) {
        this.treatmentInitiationDate = treatmentInitiationDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getBmi() {
        return bmi;
    }

    public void setBmi(Float bmi) {
        this.bmi = bmi;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }


}
