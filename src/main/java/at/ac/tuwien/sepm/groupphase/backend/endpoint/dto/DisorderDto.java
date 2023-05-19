package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.entity.DisorderStrings;

import java.util.List;
import java.util.Map;

public record DisorderDto(Long id, Integer version, Map<LanguageEnum.Language, DisorderStrings> text,  List<Long> modules, String logic, Boolean deleted) {
}
