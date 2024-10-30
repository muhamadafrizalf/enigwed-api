package com.enigwed.record;

import lombok.*;

import java.nio.file.Path;

@Builder
public record SaveImage(Path filePath, String uniqueFilename, String contentType, long size) {
}
