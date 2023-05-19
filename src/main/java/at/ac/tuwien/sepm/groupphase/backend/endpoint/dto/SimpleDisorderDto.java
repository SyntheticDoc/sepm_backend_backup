package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.DisorderStrings;

import java.util.List;


public record SimpleDisorderDto(Long id, Integer version, DisorderStrings text, List<Long> modules, String logic, Boolean deleted) {
}

