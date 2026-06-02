package com.serenity.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAttributeDto {

    private Map<String, List<String>> attributes;

    public UserAttributeDto() {
    }

    public UserAttributeDto(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }
}
