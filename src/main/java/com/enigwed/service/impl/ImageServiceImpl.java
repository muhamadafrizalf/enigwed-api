package com.enigwed.service.impl;

import com.enigwed.constant.SErrorMessage;
import com.enigwed.constant.SMessage;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.ImageResponse;
import com.enigwed.entity.Image;
import com.enigwed.record.SaveImage;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.ImageRepository;
import com.enigwed.service.ImageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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
                throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SMessage.ERROR_CREATING_IMAGE_DIRECTORY, e.getMessage());
            }
        }
    }

    private Image findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.ID_IS_REQUIRED);
        return imageRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SMessage.IMAGE_NOT_FOUND));
    }

    private SaveImage saveImageToDirectory(MultipartFile file) throws IOException {
        if (!List.of("image/jpeg", "image/png", "image/jpg", "image/svg+xml").contains(file.getContentType())) {
            throw new ConstraintViolationException(SMessage.INVALID_IMAGE_TYPE, null);
        }
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.lastIndexOf('.') > 0)
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".jpg";
        String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID() + extension;
        Path filePath = directoryPath.resolve(uniqueFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath);
        }
        String contentType = file.getContentType();
        long size = file.getSize();
        return new SaveImage(filePath.toString(), uniqueFilename, contentType, size);
    }

    private void deleteImageFromDirectory(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new IOException("Failed to delete file: " + path, e);
            }
        } else {
            log.warn("File not found for deletion: {}", filePath);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Image createImage(MultipartFile file) {
        try {
            Image image = new Image();
            if (file != null && !file.isEmpty()) {
                // ConstraintViolationException & IOException
                SaveImage savedImage = saveImageToDirectory(file);
                image.setName(savedImage.uniqueFilename());
                image.setPath(savedImage.filePath());
                image.setContentType(savedImage.contentType());
                image.setSize(savedImage.size());
            }
            return imageRepository.saveAndFlush(image);
        } catch (ConstraintViolationException e) {
            log.error("Constraint violation error during creation image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.ERROR_CREATING_IMAGE, e.getMessage());
        } catch (IOException e) {
            log.error("IO error during creation image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SMessage.ERROR_CREATING_IMAGE, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during creation image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Image updateImage(String imageId, MultipartFile updatedImage) {
        try {
            // ErrorResponse
            Image image = findByIdOrThrow(imageId);
            // IOException
            if (image.getPath() != null) deleteImageFromDirectory(image.getPath());
            // ConstraintViolationException & IOException
            SaveImage newImage = saveImageToDirectory(updatedImage);
            image.setName(newImage.uniqueFilename());
            image.setPath(newImage.filePath());
            image.setContentType(newImage.contentType());
            image.setSize(newImage.size());
            return imageRepository.saveAndFlush(image);
        } catch (ConstraintViolationException e) {
            log.error("Constraint violation error during update image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (IOException e) {
            log.error("IO error during update image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during update image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteImage(String id) {
        try {
            // ErrorResponse
            Image image = findByIdOrThrow(id);
            // IOException
            deleteImageFromDirectory(image.getPath());
            imageRepository.deleteById(id);
        } catch (IOException e) {
            log.error("IO error during deleting image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deleting image: {}", e.getError());
            throw e;
        }
    }

    @Override
    public Image softDeleteImageById(String id) {
        try {
            // ErrorResponse
            Image image = findByIdOrThrow(id);
            // IOException
            deleteImageFromDirectory(image.getPath());
            image.setName(null);
            image.setPath(null);
            image.setContentType(null);
            image.setSize(null);
            return imageRepository.saveAndFlush(image);
        } catch (IOException e) {
            log.error("IO error during deletion image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deletion image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Resource loadImageResourceById(String id) {
        try {
            Image image = findByIdOrThrow(id);
            if (image == null) return null;
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath)) {
                throw new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SMessage.IMAGE_NOT_FOUND);
            }
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            log.error("IO error during loading image image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SMessage.FETCHING_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during loading image image: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<ImageResponse> findByIdResponse(String id) {
        Image image = findByIdOrThrow(id);

        ImageResponse response = ImageResponse.from(image);
        return ApiResponse.success(response, SMessage.IMAGE_FOUND);
    }

    @Override
    public ApiResponse<ImageResponse> updateResponse(String imageId, MultipartFile updatedImage) {
        try {
            Image image = findByIdOrThrow(imageId);
            deleteImageFromDirectory(image.getPath());
            SaveImage newImage = saveImageToDirectory(updatedImage);

            image.setName(newImage.uniqueFilename());
            image.setPath(newImage.filePath());
            image.setContentType(newImage.contentType());
            image.setSize(newImage.size());

            image = imageRepository.saveAndFlush(image);
            
            ImageResponse response = ImageResponse.from(image);

            return ApiResponse.success(response, SMessage.IMAGE_UPDATED);
        } catch (ConstraintViolationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SMessage.UPDATE_FAILED, e.getMessage());
        }
    }


}
