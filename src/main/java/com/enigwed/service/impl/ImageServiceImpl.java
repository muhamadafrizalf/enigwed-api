package com.enigwed.service.impl;

import com.enigwed.constant.Message;
import com.enigwed.entity.Image;
import com.enigwed.record.SaveImage;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.ImageRepository;
import com.enigwed.service.ImageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    @Value("${enigwed.multipart.path-location}")
    private Path directoryPath;

    @PostConstruct
    public void initDirectory() {
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e) {
                throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.ERROR_CREATING_IMAGE_DIRECTORY, e.getMessage());
            }
        }
    }

    private Image findByIdOrThrow(String id) {
        if (id == null || id.isEmpty())
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, Message.ID_IS_REQUIRED);
        return imageRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, Message.IMAGE_NOT_FOUND));
    }

    private SaveImage saveImage(MultipartFile file) throws IOException {
        if (!List.of("image/jpeg", "image/png", "image/jpg", "image/svg+xml").contains(file.getContentType())) {
            throw new ConstraintViolationException(Message.INVALID_IMAGE_TYPE, null);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID() + extension;

        Path filePath = directoryPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        String contentType = file.getContentType();
        long size = file.getSize();

        return new SaveImage(filePath, uniqueFilename, contentType, size);
    }

    private void deleteImage(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath))
            throw new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, Message.IMAGE_NOT_FOUND);
        Files.delete(filePath);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Image create(MultipartFile file) {
        try {
            SaveImage savedImage = saveImage(file);
            Image image = Image.builder()
                    .name(savedImage.uniqueFilename())
                    .path(savedImage.filePath().toString())
                    .contentType(savedImage.contentType())
                    .size(savedImage.size())
                    .build();
            imageRepository.saveAndFlush(image);
            return image;
        } catch (ConstraintViolationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.ERROR_CREATING_IMAGE, e.getMessage());
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.ERROR_CREATING_IMAGE, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Resource findById(String id) {
        try {
            Image image = findByIdOrThrow(id);
            if (image == null) return null;
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath)) {
                throw new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, Message.IMAGE_NOT_FOUND);
                }
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.FETCHING_FAILED, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Image update(String imageId, MultipartFile updatedImage) {
        try {
            Image image = findByIdOrThrow(imageId);
            deleteImage(image.getPath());
            SaveImage newImage = saveImage(updatedImage);

            image.setName(newImage.uniqueFilename());
            image.setPath(newImage.filePath().toString());
            image.setContentType(newImage.contentType());
            image.setSize(newImage.size());

            return imageRepository.saveAndFlush(image);
        } catch (ConstraintViolationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.ERROR_CREATING_IMAGE, e.getMessage());
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.UPDATE_FAILED, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        try {
            Image image = findByIdOrThrow(id);
            deleteImage(image.getPath());
            imageRepository.deleteById(id);
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.DELETE_FAILED, e.getMessage());
        }
    }
}
