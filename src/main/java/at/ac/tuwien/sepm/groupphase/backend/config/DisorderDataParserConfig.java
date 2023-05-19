package at.ac.tuwien.sepm.groupphase.backend.config;

import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.DisorderDataMapper;
import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.DisorderDataParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("at.ac.tuwien.sepm.groupphase.backend.helper.dataparser")
public class DisorderDataParserConfig {

    @Bean
    public DisorderDataParser disorderDataParser() {
        return new DisorderDataParser();
    }

    @Bean
    public DisorderDataMapper disorderDataMapper() {
        return new DisorderDataMapper();
    }
}
