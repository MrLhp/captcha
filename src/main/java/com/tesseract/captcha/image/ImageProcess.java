package com.tesseract.captcha.image;

import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReaderSpi;
import com.google.code.kaptcha.Producer;
import com.tesseract.captcha.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class ImageProcess {
    static String splitPath = "d:\\tmp\\yzm\\split\\";

    public void imageProcess(Producer captchaProducer,int no) {
        try {
            int count = 0;
            for (int i=1;i<=5000;i++) {
                final String capText = captchaProducer.createText();
                BufferedImage bi = captchaProducer.createImage(capText);
                bi = ImageUtils.removeBackground(bi);
                bi = ImageUtils.ImageProcessing(bi);
                List<BufferedImage> imageList = ImageUtils.splitImage(bi);
                if (imageList.size()<=4) {
                    for (int j = 1; j <= imageList.size(); j++) {
                        File splitFile = new File(splitPath + count + "_" + capText.substring(j-1,j) + ".jpeg");
                        ImageIO.write(imageList.get(j-1),"jpeg", splitFile);
                        File tif = ImageUtils.png2Tif(splitFile);
                        IIOImage img = new IIOImage(ImageIO.read(tif), null, null);
                        ImageUtils.handleTIFFDpi(img.getRenderedImage(),tif.getAbsolutePath(),300);
                        count++;
                    }
                }else{
                    this.log.info("抛弃验证码{}", capText);
                }
            }
            genMergeTif(no);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        try {
            genMergeTif(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void genMergeTif(int no) throws IOException {
        String fileName = "d:/tmp/yzm/merge/captcha.normal.exp" + no;
        List<File> fileList = Stream.of(new File("d:/tmp/yzm/split/").listFiles())
                .flatMap(file -> file.listFiles() == null ?
                        Stream.of(file) : Stream.of(file.listFiles())).filter(file -> file.getName().endsWith("tif"))
                .collect(LinkedList::new,LinkedList::add,LinkedList::addAll);
        fileList=sortFileByName(fileList, "asc");


        FileOutputStream fileOutputStream = new FileOutputStream(fileName + ".box");
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");

        for (int i = 0;i<fileList.size();i++) {
            File file = fileList.get(i);
            BufferedImage bufferedImage = ImageIO.read(file);
            String substring = file.getName().substring(0, file.getName().length() - 4);
            StringBuilder sb = new StringBuilder(substring.split("_")[1]);
            sb.append(" 0 0 ").append(bufferedImage.getWidth()).append(" ").append(bufferedImage.getHeight());
            sb.append(" ").append(i).append("\n");

            outputStreamWriter.write(sb.toString());
            outputStreamWriter.flush();
        }
        outputStreamWriter.close();
        ImageUtils.tif2Marge(fileList,new File(fileName+".tif"),300,300);
    }

    private static void genTesseractBox(int no) throws IOException {
        FileImageInputStream fis = null;
        TIFFImageReaderSpi tiffImageReaderSpi = new TIFFImageReaderSpi();
        TIFFImageReader tiffImageReader = new TIFFImageReader(tiffImageReaderSpi);
        fis = new FileImageInputStream(new File("d:/tmp/yzm/merge/captcha.normal.exp"+no+".tif"));
        tiffImageReader.setInput(fis);
        int numPages = tiffImageReader.getNumImages(true);
        for (int i = 0; i < numPages; i++) {
            BufferedImage bi = tiffImageReader.read(i);
            System.out.println(i+" 0 0 "+(bi.getWidth()-1)+" "+(bi.getHeight()-2)+" "+i);
        }
    }

    private static List<File> sortFileByName(List<File> files, final String orderStr) {
        if (!orderStr.equalsIgnoreCase("asc") && orderStr.equalsIgnoreCase("desc")) {
            return files;
        }
        File[] files1 = files.toArray(new File[0]);
        Arrays.sort(files1, new Comparator<File>() {
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName().split("_")[0]);
                int n2 = extractNumber(o2.getName().split("_")[0]);
                if(orderStr == null || orderStr.length() < 1 || orderStr.equalsIgnoreCase("asc")) {
                    return n1 - n2;
                } else {
                    //降序
                    return n2 - n1;
                }
            }
        });

        return Arrays.asList(files1);
    }

    private static int extractNumber(String name) {
        int i;
        try {
            String number = name.replaceAll("[^\\d]", "");
            i = Integer.parseInt(number);
        } catch (Exception e) {
            i = 0;
        }
        return i;
    }
}
