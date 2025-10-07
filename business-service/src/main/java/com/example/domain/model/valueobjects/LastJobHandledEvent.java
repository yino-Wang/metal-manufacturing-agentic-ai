package com.example.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;


@Embeddable
public class LastJobHandledEvent {

    private Integer handlingEventId;
    @Transient
    private String handlingEventType;
    @Transient
    private String handlingEventRequiredMaterials;
    @Transient
    private String handlingEventProductionStep;
    // Null object pattern.
    public static final LastJobHandledEvent EMPTY = new LastJobHandledEvent();

    public LastJobHandledEvent(){}

    public LastJobHandledEvent(Integer handlingEventId, String handlingEventType, String handlingEventRequiredMaterials, String handlingEventProductionStep){
        this.handlingEventId = handlingEventId;
        this.handlingEventType = handlingEventType;
        this.handlingEventRequiredMaterials = handlingEventRequiredMaterials;
        this.handlingEventProductionStep = handlingEventProductionStep;
    }

    public String getHandlingEventType(){return this.handlingEventType;}

    public String getHandlingEventRequiredMaterials(){return this.handlingEventRequiredMaterials;}

    public Integer getHandlingEventId(){return this.handlingEventId;}

    public void setHandlingEventType(String handlingEventType){this.handlingEventType = handlingEventType;}

    public void setHandlingEventId(Integer handlingEventId){this.handlingEventId = handlingEventId;}

    public void sethHandlingEventRequiredMaterials(String handlingEventRequiredMaterials){this.handlingEventRequiredMaterials = handlingEventRequiredMaterials;}

    public void setHandlingEventProductionStep(String handlingEventProductionStep){this.handlingEventProductionStep = handlingEventProductionStep;}

    public String getHandlingEventProductionStep(){return this.handlingEventProductionStep;}


}
