package com.pickyfy.pickyfy.web.dto.request;


public record SavedPlaceRequest (
        String name,
        String description,
        boolean isPublic,
        Long userId
){}
