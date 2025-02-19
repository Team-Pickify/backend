package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.MagazineRequest;
import com.pickyfy.pickyfy.web.dto.response.MagazineResponse;
import java.util.List;

public interface MagazineService {
    Long createMagazine(MagazineRequest request);
    MagazineResponse getMagazine(Long id);
    List<MagazineResponse> getAllMagazines();
    void updateMagazine(Long id, MagazineRequest request);
    void deleteMagazine(Long id);
}
