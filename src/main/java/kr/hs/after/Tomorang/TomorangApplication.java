package kr.hs.after.Tomorang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("kr.hs.after.Tomorang.DAO")  // ← 이거 있어야 함!
public class TomorangApplication {

    public static void main(String[] args) {
        SpringApplication.run(TomorangApplication.class, args);
    }

}
