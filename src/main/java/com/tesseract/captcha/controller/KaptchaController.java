package com.tesseract.captcha.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.tesseract.captcha.image.ImageProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@RestController
public class KaptchaController {
    private Producer captchaProducer = null;

    @Autowired
    public void setCaptchaProducer(final Producer captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    @RequestMapping(value = "/start")
    public void processCapt() {
        this.log.info("开始了");
        ImageProcess imageProcess = new ImageProcess();
        imageProcess.imageProcess(captchaProducer);
        this.log.info("结束了");
    }

    @RequestMapping(value = "/valid")
    public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        // Set to expire far in the past.
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");

        // return a jpeg
        response.setContentType("image/jpeg");

        // create the text for the image
        final String capText = this.captchaProducer.createText();

        // store the text in the session
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

        // create the image with the text
        final BufferedImage bi = this.captchaProducer.createImage(capText);

        final ServletOutputStream out = response.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }

}
