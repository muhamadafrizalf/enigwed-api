package com.enigwed.service.impl;

import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
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
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return imageRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, Message.IMAGE_NOT_FOUND));
    }

    private SaveImage savePathImage(MultipartFile file) throws IOException {
        if (!List.of("image/jpeg", "image/png", "image/jpg", "image/svg+xml").contains(file.getContentType())) {
            throw new ConstraintViolationException(Message.INVALID_IMAGE_TYPE, null);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID() + extension;

        Path tempFile = Files.createTempFile("upload-", uniqueFilename);
        Files.write(tempFile, file.getBytes());

        Path filePath = directoryPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        String contentType = file.getContentType();
        long size = file.getSize();

        return new SaveImage(filePath.toString(), uniqueFilename, contentType, size);
    }

    private void deletePathImage(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath))
            throw new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, Message.IMAGE_NOT_FOUND);
        Files.delete(filePath);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Image createImage(MultipartFile file) {
        try {
            Image image = new Image();
            if (file != null && !file.isEmpty()) {
                // ConstraintViolationException & IOException
                SaveImage savedImage = savePathImage(file);
                image.setName(savedImage.uniqueFilename());
                image.setPath(savedImage.filePath());
                image.setContentType(savedImage.contentType());
                image.setSize(savedImage.size());
            }
            return imageRepository.saveAndFlush(image);
        } catch (ConstraintViolationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.ERROR_CREATING_IMAGE, e.getMessage());
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.ERROR_CREATING_IMAGE, e.getMessage());
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
                throw new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, Message.IMAGE_NOT_FOUND);
                }
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.FETCHING_FAILED, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Image updateImage(String imageId, MultipartFile updatedImage) {
        try {
            Image image = findByIdOrThrow(imageId);
            deletePathImage(image.getPath());
            SaveImage newImage = savePathImage(updatedImage);
            image.setName(newImage.uniqueFilename());
            image.setPath(newImage.filePath());
            image.setContentType(newImage.contentType());
            image.setSize(newImage.size());
            return imageRepository.saveAndFlush(image);
        } catch (ConstraintViolationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, e.getMessage());
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.UPDATE_FAILED, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteImage(String id) {
        try {
            Image image = findByIdOrThrow(id);
            deletePathImage(image.getPath());
            imageRepository.deleteById(id);
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.DELETE_FAILED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<ImageResponse> findByIdResponse(String id) {
        Image image = findByIdOrThrow(id);

        ImageResponse response = ImageResponse.from(image);
        return ApiResponse.success(response, Message.IMAGE_FOUND);
    }

    @Override
    public ApiResponse<ImageResponse> updateResponse(String imageId, MultipartFile updatedImage) {
        try {
            Image image = findByIdOrThrow(imageId);
            deletePathImage(image.getPath());
            SaveImage newImage = savePathImage(updatedImage);

            image.setName(newImage.uniqueFilename());
            image.setPath(newImage.filePath());
            image.setContentType(newImage.contentType());
            image.setSize(newImage.size());

            image = imageRepository.saveAndFlush(image);
            
            ImageResponse response = ImageResponse.from(image);

            return ApiResponse.success(response, Message.IMAGE_UPDATED);
        } catch (ConstraintViolationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, e.getMessage());
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.UPDATE_FAILED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> softDeleteById(String id) {
        try {
            Image image = findByIdOrThrow(id);
            deletePathImage(image.getPath());
            image.setName(null);
            image.setPath(null);
            image.setContentType(null);
            image.setSize(null);
            imageRepository.saveAndFlush(image);
            return ApiResponse.success(Message.IMAGE_DELETED);
        } catch (IOException e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.DELETE_FAILED, e.getMessage());
        }
    }
}
