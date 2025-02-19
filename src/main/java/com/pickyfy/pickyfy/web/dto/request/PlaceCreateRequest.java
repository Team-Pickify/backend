package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.common.AllFieldsNotNull;

import java.math.BigDecimal;

@AllFieldsNotNull
public record PlaceCreateRequest (
  String name,
  String shortDescription,
  String address,
  String instagramLink,
  String naverPlaceLink,
  BigDecimal latitude,
  BigDecimal longitude,
  Long categoryId,
  Long magazineId
){}
