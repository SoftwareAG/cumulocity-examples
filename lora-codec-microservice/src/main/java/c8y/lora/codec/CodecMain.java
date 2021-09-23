package c8y.lora.codec;

import c8y.lora.sdk.service.MicroserviceCustomCodecApp;
import org.springframework.boot.SpringApplication;

@MicroserviceCustomCodecApp
public class CodecMain {
    public static void main(String[] args) {
        SpringApplication.run(CodecMain.class, args);
    }
}
