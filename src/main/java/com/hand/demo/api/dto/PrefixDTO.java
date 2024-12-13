package com.hand.demo.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrefixDTO {
    private String bucketname;
    private String directory;
    private String contentType;
    private String storageUnit;
    private String storageSize;
}
