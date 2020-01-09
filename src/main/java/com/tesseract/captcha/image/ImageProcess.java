package com.tesseract.captcha.image;

import com.google.code.kaptcha.Producer;
import com.tesseract.captcha.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;

public class ImageProcess {
    private Producer captchaProducer = null;

    @Autowired
    public void setCaptchaProducer(final Producer captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    public void imageProcess() {
        try {
            final String capText = this.captchaProducer.createText();
            BufferedImage bi = this.captchaProducer.createImage(capText);
            bi = ImageUtils.removeBackground(bi);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
