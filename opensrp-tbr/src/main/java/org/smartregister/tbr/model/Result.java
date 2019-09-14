package org.smartregister.tbr.model;

import java.util.Date;

/**
 * Created by samuelgithengi on 11/16/17.
 */

public class Result {

    private Long id;
    private String baseEntityId;
    private String type;
    private String result1;
    private String value1;
    private String result2;
    private String value2;
    private String formSubmissionId;
    private String eventId;
    private Date date;
    private String anmId;
    private String locationId;
    private String syncStatus;
    private String createdAt;
    private Long updatedAt;
    private String heightWeightDate;

    /*
     * START - ADDING FIELDS FOR CHILD HEALTH INDICATORS
     */

    private Float height;
    private Float weight;
    private String heightAgeStatus;
    private String weightHeightStatus;
    private Float haemoglobin;
    private String nextVisitDate;
    private String nextGrowthMonitoringDate;
    private String deworming;
    private String dewormingDate;
    private String diarrea;
    private String malaria;
    private String cold;
    private String pneumonia;
    private String bronchitis;

    private String zeroTuberculosis;
    private String zeroAntiherpatitis;
    private String twoAntipolio;
    private String twoPentavalente;
    private String twoNeumococo;
    private String twoRotavirus;
    private String fourAntipolio;
    private String fourPentavalente;
    private String fourNeumococo;
    private String fourRotavirus;
    private String sixAntipolio;
    private String sixPentavalente;
    private String twelveNeumococo;
    private String twelveSarampion;
    private String fifteenAntiamarilica;

    /*
     * END - ADDING FIELDS FOR CHILD HEALTH INDICATORS
     */

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult1() {
        return result1;
    }

    public void setResult1(String result1) {
        this.result1 = result1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getResult2() {
        return result2;
    }

    public void setResult2(String result2) {
        this.result2 = result2;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAnmId() {
        return anmId;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
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

    public Float getHaemoglobin() {
        return haemoglobin;
    }

    public void setHaemoglobin(Float haemoglobin) {
        this.haemoglobin = haemoglobin;
    }

    public String getNextVisitDate() {
        return nextVisitDate;
    }

    public void setNextVisitDate(String nextVisitDate) {
        this.nextVisitDate = nextVisitDate;
    }

    public String getNextGrowthMonitoringDate() {
        return nextGrowthMonitoringDate;
    }

    public void setNextGrowthMonitoringDate(String nextGrowthMonitoringDate) {
        this.nextGrowthMonitoringDate = nextGrowthMonitoringDate;
    }

    public String getDiarrea() {
        return diarrea;
    }

    public void setDiarrea(String diarrea) {
        this.diarrea = diarrea;
    }

    public String getMalaria() {
        return malaria;
    }

    public void setMalaria(String malaria) {
        this.malaria = malaria;
    }

    public String getCold() {
        return cold;
    }

    public void setCold(String cold) {
        this.cold = cold;
    }

    public String getPneumonia() {
        return pneumonia;
    }

    public void setPneumonia(String pneumonia) {
        this.pneumonia = pneumonia;
    }

    public String getHeightAgeStatus() {
        return heightAgeStatus;
    }

    public void setHeightAgeStatus(String heightAgeStatus) {
        this.heightAgeStatus = heightAgeStatus;
    }

    public String getWeightHeightStatus() {
        return weightHeightStatus;
    }

    public void setWeightHeightStatus(String weightHeightStatus) {
        this.weightHeightStatus = weightHeightStatus;
    }

    public String getDeworming() {
        return deworming;
    }

    public void setDeworming(String deworming) {
        this.deworming = deworming;
    }

    public String getDewormingDate() {
        return dewormingDate;
    }

    public void setDewormingDate(String dewormingDate) {
        this.dewormingDate = dewormingDate;
    }

    public String getZeroTuberculosis() {
        return zeroTuberculosis;
    }

    public void setZeroTuberculosis(String zeroTuberculosis) {
        this.zeroTuberculosis = zeroTuberculosis;
    }

    public String getZeroAntiherpatitis() {
        return zeroAntiherpatitis;
    }

    public void setZeroAntiherpatitis(String zeroAntiherpatitis) {
        this.zeroAntiherpatitis = zeroAntiherpatitis;
    }

    public String getTwoAntipolio() {
        return twoAntipolio;
    }

    public void setTwoAntipolio(String twoAntipolio) {
        this.twoAntipolio = twoAntipolio;
    }

    public String getTwoPentavalente() {
        return twoPentavalente;
    }

    public void setTwoPentavalente(String twoPentavalente) {
        this.twoPentavalente = twoPentavalente;
    }

    public String getTwoNeumococo() {
        return twoNeumococo;
    }

    public void setTwoNeumococo(String twoNeumococo) {
        this.twoNeumococo = twoNeumococo;
    }

    public String getTwoRotavirus() {
        return twoRotavirus;
    }

    public void setTwoRotavirus(String twoRotavirus) {
        this.twoRotavirus = twoRotavirus;
    }

    public String getFourAntipolio() {
        return fourAntipolio;
    }

    public void setFourAntipolio(String fourAntipolio) {
        this.fourAntipolio = fourAntipolio;
    }

    public String getFourPentavalente() {
        return fourPentavalente;
    }

    public void setFourPentavalente(String fourPentavalente) {
        this.fourPentavalente = fourPentavalente;
    }

    public String getFourNeumococo() {
        return fourNeumococo;
    }

    public void setFourNeumococo(String fourNeumococo) {
        this.fourNeumococo = fourNeumococo;
    }

    public String getFourRotavirus() {
        return fourRotavirus;
    }

    public void setFourRotavirus(String fourRotavirus) {
        this.fourRotavirus = fourRotavirus;
    }

    public String getSixAntipolio() {
        return sixAntipolio;
    }

    public void setSixAntipolio(String sixAntipolio) {
        this.sixAntipolio = sixAntipolio;
    }

    public String getSixPentavalente() {
        return sixPentavalente;
    }

    public void setSixPentavalente(String sixPentavalente) {
        this.sixPentavalente = sixPentavalente;
    }

    public String getTwelveNeumococo() {
        return twelveNeumococo;
    }

    public void setTwelveNeumococo(String twelveNeumococo) {
        this.twelveNeumococo = twelveNeumococo;
    }

    public String getTwelveSarampion() {
        return twelveSarampion;
    }

    public void setTwelveSarampion(String twelveSarampion) {
        this.twelveSarampion = twelveSarampion;
    }

    public String getFifteenAntiamarilica() {
        return fifteenAntiamarilica;
    }

    public void setFifteenAntiamarilica(String fifteenAntiamarilica) {
        this.fifteenAntiamarilica = fifteenAntiamarilica;
    }

    public String getBronchitis() {
        return bronchitis;
    }

    public void setBronchitis(String bronchitis) {
        this.bronchitis = bronchitis;
    }

    public String getHeightWeightDate() {
        return heightWeightDate;
    }

    public void setHeightWeightDate(String heightWeightDate) {
        this.heightWeightDate = heightWeightDate;
    }

}
