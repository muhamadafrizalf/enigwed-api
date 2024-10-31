package com.enigwed.service;

import com.enigwed.entity.Image;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    Image create(MultipartFile file);
    Resource findById(String id);
    Image update(String id, MultipartFile image);
    void deleteById(String id);
}
