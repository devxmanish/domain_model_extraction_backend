package com.devxmanish.DomainModelExtraction.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response<T> {
    private int statusCode;
    private String message;
    private T data; // actual payload to return
}
