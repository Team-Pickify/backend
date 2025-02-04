package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    void logout(String accessToken);
}
