package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.MagazineCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.MagazineUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.MagazineResponse;
import java.util.List;

public interface MagazineService {
    Long createMagazine(MagazineCreateRequest request);
    MagazineResponse getMagazine(Long id);
    List<MagazineResponse> getAllMagazines();
    void updateMagazine(Long id, MagazineUpdateRequest request);
    void deleteMagazine(Long id);
}
