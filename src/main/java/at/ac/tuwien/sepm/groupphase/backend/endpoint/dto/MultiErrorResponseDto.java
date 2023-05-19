package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * Error response for multiple errors like validation
 * errors.
 */
public record MultiErrorResponseDto(
    List<ErrorResponseDto> errors
){}
