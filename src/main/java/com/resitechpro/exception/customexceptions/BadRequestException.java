package com.resitechpro.exception.customexceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class BadRequestException extends Exception {
    private final String message;
}
