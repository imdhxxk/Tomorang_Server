package kr.hs.after.Tomorang.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final MappingJackson2HttpMessageConverter octetStreamConverter;

    public WebConfig(MappingJackson2HttpMessageConverter octetStreamConverter) {
        this.octetStreamConverter = octetStreamConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // octet-stream 미디어 타입을 JSON 컨버터에 추가
        List<MediaType> supportedMediaTypes = new ArrayList<>(octetStreamConverter.getSupportedMediaTypes());
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        octetStreamConverter.setSupportedMediaTypes(supportedMediaTypes);
    }
}
