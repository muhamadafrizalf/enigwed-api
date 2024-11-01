package com.enigwed.service.impl;

import com.enigwed.constant.ErrorMessage;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.repository.WeddingOrganizerRepository;
import com.enigwed.service.WeddingOrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeddingOrganizerImpl implements WeddingOrganizerService {
    private final WeddingOrganizerRepository weddingOrganizerRepository;

    @Override
    public void createWeddingOrganizer(WeddingOrganizer weddingOrganizer) {
        if (weddingOrganizerRepository.countByPhoneAndDeletedAtIsNull(weddingOrganizer.getPhone()) > 0) throw new DataIntegrityViolationException(ErrorMessage.PHONE_ALREADY_EXIST);
        if (weddingOrganizerRepository.countByNibAndDeletedAtIsNull(weddingOrganizer.getNib()) > 0) throw new DataIntegrityViolationException(ErrorMessage.NIB_ALREADY_EXIST);
        if (weddingOrganizerRepository.countByNpwpAndDeletedAtIsNull(weddingOrganizer.getNpwp()) > 0) throw new DataIntegrityViolationException(ErrorMessage.NPWP_ALREADY_EXIST);
        weddingOrganizerRepository.saveAndFlush(weddingOrganizer);
    }

    @Override
    public ApiResponse<WeddingOrganizer> update(WeddingOrganizer weddingOrganizer) {
        return null;
    }

    @Override
    public ApiResponse<WeddingOrganizer> findById(Long id) {
        return null;
    }

    @Override
    public ApiResponse<List<WeddingOrganizer>> findAll() {
        return null;
    }

    @Override
    public ApiResponse<?> deleteById(String id) {
        return null;
    }

    @Override
    public ApiResponse<List<WeddingOrganizer>> search(String keyword) {
        return null;
    }
}
