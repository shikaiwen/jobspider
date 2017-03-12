import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * Created by root on 3/11/2017.
 * https://github.com/tesseract-ocr/tesseract
 */
public class OCRTest {

    public static void main(String[] args) {
        t1();
    }

    static void t1(){

//        ProcessBuilder processBuilder = new ProcessBuilder();
//        Map<String, String> environment = processBuilder.environment();
//        System.out.println(JSON.toJSONString(environment));
//        if(true) return;


        File imageFile = new File("I:/test_area/yzm.png");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
//        File tessdata = LoadLibs.
//        instance.setDatapath(tessdata.getAbsolutePath());

//        instance.setDatapath("F:\\maven_repository\\net\\sourceforge\\tess4j\\tess4j\\3.2.2\\tess4j-3.2.2");
        try {
            String s = instance.doOCR(imageFile);
            System.out.println(s);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
    }
}
