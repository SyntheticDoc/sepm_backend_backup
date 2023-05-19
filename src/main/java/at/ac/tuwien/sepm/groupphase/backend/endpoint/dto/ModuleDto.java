package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public record ModuleDto(Long id, Integer version, List<Long> questions, String logic, Boolean deleted) {
}
