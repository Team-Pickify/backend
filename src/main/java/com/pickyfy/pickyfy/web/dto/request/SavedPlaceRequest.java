package com.pickyfy.pickyfy.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public record SavedPlaceRequest (
        String name,
        String description,
        boolean isPublic,
        Long userId
){

}
