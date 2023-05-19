package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DisorderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DisorderMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;
import at.ac.tuwien.sepm.groupphase.backend.entity.IdVersionKey;
import at.ac.tuwien.sepm.groupphase.backend.repository.DisorderRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.DisorderService;
import at.ac.tuwien.sepm.groupphase.backend.service.LogicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisorderServiceImpl implements DisorderService {

    DisorderRepository disorderRepository;
    DisorderMapper disorderMapper;
    LogicService logicService;

    public DisorderServiceImpl(DisorderRepository disorderRepository, DisorderMapper disorderMapper, LogicService logicService) {
        this.disorderRepository = disorderRepository;
        this.disorderMapper = disorderMapper;
        this.logicService = logicService;
    }

    public List<Disorder> getAllDisorders() {
        return disorderRepository.findLatestAll();
    }

    @Override
    public Disorder addDisorder(DisorderDto disorder) {
        // Check for parsing error
        logicService.parseString(disorder.logic(), null);

        Disorder newDisorder = disorderMapper.disorderDtoToDisorder(disorder);
        Long id = disorderRepository.getNextId();
        if (id == null) {
            id = 0L;
        }
        newDisorder.setId(id);
        newDisorder.setDeleted(false);
        newDisorder.setVersion(0);
        return disorderRepository.saveAndFlush(newDisorder);
    }

    @Override
    public Disorder updateDisorder(DisorderDto disorder) {
        // Check for parsing error
        logicService.parseString(disorder.logic(), null);

        Disorder newDisorder = disorderMapper.disorderDtoToDisorder(disorder);
        newDisorder.setVersion(disorderRepository.findLatest(newDisorder.getId()).getVersion() + 1);
        return disorderRepository.save(newDisorder);
    }

    @Override
    public void deleteDisorder(Long id) {
        Disorder newDisorder = disorderRepository.findLatest(id);
        newDisorder.setVersion(disorderRepository.findLatest(newDisorder.getId()).getVersion() + 1);
        newDisorder.setDeleted(true);
        disorderRepository.save(newDisorder);
    }

    @Override
    public Disorder getSpecificDisorder(Long id, int version) {
        return disorderRepository.findById(new IdVersionKey(id, version)).orElse(null);
    }

    @Override //ToDo Return null
    public Disorder getDisorderById(Long id) {
        return disorderRepository.findLatest(id);
    }
}
