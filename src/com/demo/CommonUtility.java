package com.demo;

import java.io.IOException;

import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;
import org.apache.log4j.Level;

public class CommonUtility{
    static String toHashKey(String FileName, String Url){
        String code = Integer.toHexString(  (FileName + Url).hashCode()  );
        
        final String HEX_PREFIX = "00000000";
        final int HASH_CODE_LEN = 8;
        
        int pos = (HEX_PREFIX + code).length() - HASH_CODE_LEN;
        String result = (HEX_PREFIX + code).substring(pos).toUpperCase();
        
        return(result);
    }
    
    static void pauseAnyKey(){
        System.out.print("Press any key...");
        try{
            System.in.read();
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println();
    }

    static void pause(double Second){
        try{
            int t = (int)(Second * 1000);
            Thread.sleep(t);
        }catch(InterruptedException E){
            /* do nothing */
        }
    }
}

