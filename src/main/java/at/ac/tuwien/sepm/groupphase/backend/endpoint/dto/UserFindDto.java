package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record UserFindDto(
    String username,
    String email
) {}
