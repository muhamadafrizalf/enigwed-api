package com.enigwed.record;

import lombok.*;

@Builder
public record SaveImage(String filePath, String uniqueFilename, String contentType, long size) {
}
