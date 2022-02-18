package com.cumulocity.lora.codec.twtg.neon.javascript.engine;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class JavaScriptEngineConfiguration {

    @Value("${javascript.engine.pool.size.max}")
    private int enginePoolMaxSize;

}
