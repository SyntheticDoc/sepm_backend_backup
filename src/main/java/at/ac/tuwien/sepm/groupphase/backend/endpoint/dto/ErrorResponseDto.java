package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

/**
 * Default error response layout.
 */
public record ErrorResponseDto(
    String message,
    int errorCode
){}
