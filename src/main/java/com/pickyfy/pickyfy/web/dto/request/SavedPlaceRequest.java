package com.pickyfy.pickyfy.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SavedPlaceRequest {
    private String name;
    private String description;
    private boolean isPublic;
    private Long userId;
}
