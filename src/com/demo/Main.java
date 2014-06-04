package com.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main{
    static Log logger = LogFactory.getLog(Main.class);

    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            logger.warn("It needs two args: filename and video-url");
            return;
        }

        String filename = args[0];
        String youtubeUrl = args[1];
        
        new Main(filename, youtubeUrl);
    }
    
    public Main(String FileName, String YoutubeUrl){
        VideoUrlParser parser = new VideoUrlParser();
        String url = parser.parse(YoutubeUrl);
        
        if(url == null){
            logger.error(parser.getLastMessage());
            error();
        }

        //VideoDownloader downloader = new VideoDownloader();
        //downloader.saveVideo(FileName, url);
        
        logger.info(GlobalData.NORMAL_TERMINATION_TEXT);
        System.exit(GlobalData.NORMAL_TERMINATION);
    }

    private void error(){
        logger.info(GlobalData.ABNORMAL_TERMINATION_TEXT);
        System.exit(GlobalData.ABNORMAL_TERMINATION);
    }
}
