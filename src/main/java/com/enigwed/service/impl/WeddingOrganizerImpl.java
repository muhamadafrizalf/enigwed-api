package com.enigwed.service.impl;

import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.repository.WeddingOrganizerRepository;
import com.enigwed.service.WeddingOrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeddingOrganizerImpl implements WeddingOrganizerService {
    private final WeddingOrganizerRepository weddingOrganizerRepository;

    @Override
    public WeddingOrganizer create(WeddingOrganizer weddingOrganizer) {
        return weddingOrganizerRepository.saveAndFlush(weddingOrganizer);
    }
}
