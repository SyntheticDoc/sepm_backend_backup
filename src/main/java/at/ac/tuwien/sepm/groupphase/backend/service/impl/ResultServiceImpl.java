package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ResultMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Argon2Parameters;
import at.ac.tuwien.sepm.groupphase.backend.entity.Result;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityData;
import at.ac.tuwien.sepm.groupphase.backend.helper.argon2crypto.Argon2PasswordEncoderWithParams;
import at.ac.tuwien.sepm.groupphase.backend.repository.Argon2ParametersRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ResultRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityDataRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ResultRepository resultRepository;
    private final Argon2ParametersRepository argon2ParametersRepository;
    private final SecurityDataRepository securityDataRepository;
    private final ResultMapper resultMapper;

    public ResultServiceImpl(ResultRepository resultRepository, Argon2ParametersRepository argon2ParametersRepository,
                             SecurityDataRepository securityDataRepository, ResultMapper resultMapper) {
        this.resultRepository = resultRepository;
        this.argon2ParametersRepository = argon2ParametersRepository;
        this.securityDataRepository = securityDataRepository;
        this.resultMapper = resultMapper;
    }

    @Override
    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    @Override
    public List<Result> getResultsForUser(String userPassword) {
        LOGGER.debug("Getting all results for user");
        SecurityData saltData = securityDataRepository.findByType("saltMapping");
        Argon2Parameters params = argon2ParametersRepository.findByType("mapping");

        Argon2PasswordEncoderWithParams argon2PasswordEncoder = new Argon2PasswordEncoderWithParams();

        String hash = argon2PasswordEncoder.encode(userPassword, params, saltData.getValue());

        return resultRepository.findResultsForUser(hash);
    }

    @Override
    public Result addResult(ResultDto resultDto) {
        Result newResult = new Result();
        /*Result newResult = resultMapper.resultDtoToResult(resultDto);*/
        return resultRepository.saveAndFlush(newResult);
    }

    @Override
    public void deleteResult(Long id) {
        resultRepository.deleteById(id);
    }

    @Override
    public Result putResult(Long id, ResultDto resultDto) {
        //TODO
        Result newResult = resultRepository.getById(id);
        /*Result resultFromDto = resultMapper.resultDtoToResult(resultDto);
        newResult.setPositiveDisorders(resultFromDto.getPositiveDisorders());
        newResult.setPositiveModules(resultFromDto.getPositiveModules());
        newResult.setPositiveQuestions(newResult.getPositiveQuestions());
        newResult.setAnsweredQuestions(newResult.getAnsweredQuestions());*/
        return resultRepository.save(newResult);
    }
}
