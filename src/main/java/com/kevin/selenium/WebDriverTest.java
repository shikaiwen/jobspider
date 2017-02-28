package com.kevin.selenium;

import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kaiwen on 28/02/2017.
 * 小坦克：http://www.cnblogs.com/TankXiao/p/5252754.html
 */
public class WebDriverTest {

    public static void main(String[] args) {

        getPageCount();
    }

    public static int getPageCount(){
        WebDriver driver = new ChromeDriver();
//        driver.get("http://blog.csdn.net/peoplelist.html?channelid=0&page=1");

        driver.get("http://blog.csdn.net/experts.html#list");
        String text = driver.findElement(By.cssSelector(".page_nav > span")).getText();
        String pattern = "[1-9]{1,}";

        Matcher matcher = Pattern.compile(pattern).matcher(text);

        if(matcher.find()){
            String itemCnt = matcher.group();
            NumberUtils.toInt(itemCnt, 0);
        }

        int pageSize = 0;
        if (matcher.find()) {
            String pageCnt = matcher.group();
            pageSize = NumberUtils.toInt(pageCnt, 0);
        }

//        System.out.println(driver.getPageSource());
        driver.close();

        return pageSize;

        //java虚拟机不能自动退出，不知为什么
//        System.exit(0);
//        WebElement loginLink = driver.findElement(By.linkText("登录"));
//        loginLink.click();
    }
}
