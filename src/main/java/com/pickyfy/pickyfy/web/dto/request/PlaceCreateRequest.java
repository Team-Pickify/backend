package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.domain.CategoryType;
import com.pickyfy.pickyfy.domain.Magazine;
import com.pickyfy.pickyfy.domain.PlaceImage;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public record PlaceCreateRequest (
  String name,
  String shortDescription,
  String address,
  String instagramLink,
  String naverPlaceLink,
  BigDecimal latitude,
  BigDecimal longitude,
  CategoryType categoryType,
  String magazineTitle
){}
