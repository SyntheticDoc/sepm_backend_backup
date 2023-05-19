package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ModuleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import org.mapstruct.Mapper;

@Mapper
public interface ModuleMapper {
    public abstract Module moduleDtoToModule(ModuleDto moduleDto);

    public abstract ModuleDto moduleToModuleDto(Module module);
}
