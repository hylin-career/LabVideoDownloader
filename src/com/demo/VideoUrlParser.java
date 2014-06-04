package com.demo;

import java.net.URLEncoder;
import java.util.List;
import java.util.logging.*;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Level;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.*;
import org.openqa.selenium.support.ui.*;

public class VideoUrlParser{
    /**
     * Converts the url to the downloadable url
     * 
     * @param url
     * @return downloadable url or null
     */
    public String parse(String YoutubeUrl){
        String youtubeDownloadableUrl = null;
        
        try{
            youtubeDownloadableUrl = _DoParse(YoutubeUrl);
        }catch(Exception E){
            String errMessage = "_DoParse '" + YoutubeUrl + "' failed, "
                    + E.getMessage();
            mLastMessage = errMessage;
            
            return(null);
        }

        return(youtubeDownloadableUrl);
    }
    
    private String _DoParse(String YoutubeUrl){
        String kejPartialUrl = "http://kej.tw/flvretriever/youtube.php?videoUrl=";
        String encodedYoutubeUrl = URLEncoder.encode(YoutubeUrl);
        
        _DisableVerboseLoggingReport();
        HtmlUnitDriver kejDriver = new HtmlUnitDriver(true);

        String kejUrl = kejPartialUrl + encodedYoutubeUrl;
        kejDriver.get(kejUrl);
        
        String linkText = "下載此檔案";
        WebElement videoInfoElement = null;
        try{
            videoInfoElement = kejDriver.findElement(By.linkText(linkText));
        }catch(NoSuchElementException E){
            mLastMessage = "findElement '" + linkText + "' - " + E.getMessage();
            return(null);
        }
        String videoInfoUrl = videoInfoElement.getAttribute("href");
        
        /* Fetch "get_video_info" */
        WebDriver videoInfoDriver = new HtmlUnitDriver();
        videoInfoDriver.get(videoInfoUrl);
        String videoInfoHtmlText = videoInfoDriver.getPageSource();
        videoInfoDriver.close();

        /* Copy "get_video_info" into textArea */
        WebElement videoInfoTextArea = kejDriver.findElement(By.id("videoInfo"));
        videoInfoTextArea.sendKeys(videoInfoHtmlText);

        /* Click "送出" button */
        String xpathButton = "/html/body/div[@class='setcenter']/div[@class='class1']/div[@id='resultarea']/input";
        WebElement submitButton = kejDriver.findElement(By.xpath(xpathButton));
        submitButton.click();

        /* Wait for the website javaScript generating "result_div" HTML block */
        WebDriverWait wait = new WebDriverWait(kejDriver, 10);
        WebElement resultDivElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("result_div")  ));

        /* Find all HTML anchor links */
        List<WebElement> links = resultDivElement.findElements(By.tagName("a"));
        Main.logger.info(links.size() + " link(s)");
        for(WebElement linkElement : links){
            String linkTxt = linkElement.getText();
            String linkUrl = linkElement.getAttribute("href");
        }
        
        String flvLinkUrl = null;
        for(WebElement linkElement : links){
            if(linkElement.getText().toUpperCase().indexOf("FLV") >= 0){
                flvLinkUrl = linkElement.getAttribute("href");
                
                break;
            }
        }
        
        kejDriver.quit();
        
        Main.logger.info("First FLV: " + flvLinkUrl);

        return(flvLinkUrl);
    }

    private void _DisableVerboseLoggingReport(){
        String[] loggingOffClasses = new String[]{
                "com.gargoylesoftware.htmlunit",
                "com.gargoylesoftware.htmlunit.WebClient",
                "com.gargoylesoftware.htmlunit.javascript",
                "com.gargoylesoftware.htmlunit.javascript.StrictErrorReporter",
                "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl",
        };

        for(String className : loggingOffClasses){
            java.util.logging.Logger.getLogger(className)
                    .setLevel(java.util.logging.Level.OFF);
        }
        
        for(String className : loggingOffClasses){
            Log4JLogger logger = (Log4JLogger) LogFactory.getLog(className);
            logger.getLogger().setLevel(Level.OFF);
        }
    }

    private static String mLastMessage = GlobalData.EMPTY_LINE;
    static String getLastMessage(){
        return(mLastMessage);
    }
}
