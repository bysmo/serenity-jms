package com.serenity.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RealmDto {

    private String realmName;
    private String displayName;
    private Boolean enabled;
    private Boolean sslRequired;

    public RealmDto() {
    }

    public RealmDto(String realmName, Boolean enabled) {
        this.realmName = realmName;
        this.enabled = enabled;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getSslRequired() {
        return sslRequired;
    }

    public void setSslRequired(Boolean sslRequired) {
        this.sslRequired = sslRequired;
    }
}
