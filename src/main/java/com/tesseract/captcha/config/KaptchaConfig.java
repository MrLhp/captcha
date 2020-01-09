package com.tesseract.captcha.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.imageio.ImageIO;
import java.util.Properties;

@Configuration
@PropertySource("classpath:kaptcha.properties")
public class KaptchaConfig {
    @Value("${kaptcha.border}")
    private String border;

    @Value("${kaptcha.border.color}")
    private String borderColor;

    @Value("${kaptcha.textproducer.font.names}")
    private String TextProducerFontNames;

    @Value("${kaptcha.textproducer.font.color}")
    private String textProducerFontColor;

    @Value("${kaptcha.textproducer.font.size}")
    private String textProducerFontSize;

    @Value("${kaptcha.textproducer.char.length}")
    private String textProducerCharLength;

    @Value("${kaptcha.textproducer.char.space}")
    private String textProducerCharSpace;

    @Value("${kaptcha.image.width}")
    private String imageWidth;

    @Value("${kaptcha.image.height}")
    private String imageHeight;

    @Value("${kaptcha.textproducer.char.string}")
    private String digit;

    @Bean
    public Producer captchaProducer() {
        // Switch off disk based caching.
        ImageIO.setUseCache(false);

        final Properties props = new Properties();
        props.put("kaptcha.border", this.border);
        props.put("kaptcha.border.color", this.borderColor);
        props.put("kaptcha.textproducer.font.names", this.TextProducerFontNames);
        props.put("kaptcha.textproducer.font.color", this.textProducerFontColor);
        props.put("kaptcha.textproducer.font.size", this.textProducerFontSize);
        props.put("kaptcha.textproducer.char.length", this.textProducerCharLength);
        props.put("kaptcha.textproducer.char.space", this.textProducerCharSpace);
        props.put("kaptcha.textproducer.char.string", this.digit);
        props.put("kaptcha.image.width", this.imageWidth);
        props.put("kaptcha.image.height", this.imageHeight);

        return new Config(props).getProducerImpl();
    }
}
