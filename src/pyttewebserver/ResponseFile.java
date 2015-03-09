/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pyttewebserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stationary
 */
public class ResponseFile extends File{
    private String mStatusCode;
    private String mRequestType;
    private String mHTTPType;
    private byte[] mBytes;
    private String mDate;
    private String mLastModified;
    private String mContentType;
    private int mContentLength; 
    
    
    
       
    /**
     *
     * @param pathName
     * @param requestType
     * @param HTTPType
     */
    public ResponseFile(String pathName, String requestType, String HTTPType){
        super(pathName);
        mStatusCode = pathName;
        mHTTPType = HTTPType;        
        mRequestType = requestType;       
        if(this.exists()){
            try {
                mBytes = Files.readAllBytes(this.toPath());
                setHeaders();               
            } catch (IOException ex) {
                Logger.getLogger(ResponseFile.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }        
    }
    
    private void setHeaders() throws IOException {
        if(mHTTPType.equals("1.1")){
            setTimes();
            mContentType = Files.probeContentType(this.toPath());
            mContentLength = mBytes.length;          
        }
        
    }
    
    /**
     * Returns the correct response from the given string
     * @return 
     * 
     * @author Hannes R from stackoverflow.com (Googled solution for getting system time)
     * @author Kristoffer Freiholtz
     */
    private void setTimes() {        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        mDate = dateFormat.format(calendar.getTime());
        mLastModified = dateFormat.format(this.lastModified());    
    }
    
    /**
     *
     * @return
     */
    public String getStatusCode(){
        switch(mStatusCode){
            case "error400.html":
                return "400 Bad Request";
            case "error404.html":
                return "404 Not Found";
            default: 
                return "200 OK";                
        }
    }
    
    public byte[] getBytes() {
        return mBytes;
    }  
    
    public String getRequestType() {
        return mRequestType;
    }

    public String getHTTPType() {
        return mHTTPType;
    }
    
    public String getDate() {
        return mDate;
    }
    
    public String getLastModified() {
        return mLastModified;
    }
    
    public String getContentType() {
        return mContentType;
    }
    
    public int getContentLength(){
        return mContentLength;
    }
    
    

    
    
}
