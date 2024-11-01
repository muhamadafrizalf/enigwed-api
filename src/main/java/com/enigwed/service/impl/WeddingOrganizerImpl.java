package com.enigwed.service.impl;

import com.enigwed.dto.response.ApiResponse;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.repository.WeddingOrganizerRepository;
import com.enigwed.service.WeddingOrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeddingOrganizerImpl implements WeddingOrganizerService {
    private final WeddingOrganizerRepository weddingOrganizerRepository;

    @Override
    public WeddingOrganizer create(WeddingOrganizer weddingOrganizer) {
        return weddingOrganizerRepository.saveAndFlush(weddingOrganizer);
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
