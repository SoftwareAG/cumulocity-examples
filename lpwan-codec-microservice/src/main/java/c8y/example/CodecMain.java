package c8y.example;

import com.cumulocity.lpwan.codec.annotation.CodecMicroserviceApplication;
import org.springframework.boot.SpringApplication;

@CodecMicroserviceApplication
public class CodecMain {
    public static void main (String[] args) {
        SpringApplication.run(CodecMain.class, args);
    }
}
