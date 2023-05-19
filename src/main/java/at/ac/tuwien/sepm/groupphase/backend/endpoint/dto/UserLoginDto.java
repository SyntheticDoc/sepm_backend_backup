package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record UserLoginDto(
    String usernameOrEmail,
    String password,
    String loginKey
) {}
