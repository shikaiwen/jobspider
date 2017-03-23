import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

        File imageFile = new File("D:\\tmp\\a.png");

        try {
            BufferedImage image = ImageIO.read(imageFile);
            System.out.println("width:"+image.getWidth() + ",height:"+image.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        File tessdata = LoadLibs.extractTessResources("tessdata");
        instance.setDatapath(tessdata.getAbsolutePath());

//        instance.setDatapath("F:\\maven_repository\\net\\sourceforge\\tess4j\\tess4j\\3.2.2\\tess4j-3.2.2");
        try {
            String s = instance.doOCR(imageFile);
            System.out.println(s);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
    }
}
