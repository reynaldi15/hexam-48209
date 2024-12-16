package com.hand.demo.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HfleUploadDTO {
    private String bucketName;
    private String directory;
    private String contentType;
    private String storageUnit;
    private Long storageSize;
    private String createdBy;
}
