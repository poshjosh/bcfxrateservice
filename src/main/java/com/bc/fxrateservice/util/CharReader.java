package com.bc.fxrateservice.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)CharReader.java   20-Oct-2014 09:57:17
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class CharReader implements Serializable {

    private transient static final Logger LOG = Logger.getLogger(CharReader.class.getName());
    
    private boolean computeCheckSum;
    
    private long checkSum;
    
    private int bufferSize = 8192;
    
    private String charset;

    public CharReader() { }

    public CharReader(String charset) { 
        this.charset = charset;
    }
    
    public CharSequence readChars(String path) throws IOException {

        return readChars(new File(path));
    }

    public CharSequence readChars(File source) throws IOException {

        if(!source.exists()) throw new FileNotFoundException(source.getPath());

        final int sourceLength = (int)source.length();
        
        LOG.fine(() -> "Source: " + source + ", source length: " + sourceLength);

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try{
            
            fis = new FileInputStream(source);
            if(charset == null) {
                isr = new InputStreamReader(fis);
            }else{
                isr = new InputStreamReader(fis, charset);
            }
            br = new BufferedReader(isr);
            
            return readChars(br, sourceLength);
        }finally{
            if(br != null) try{ br.close(); }catch(IOException e){
                LOG.log(Level.WARNING, null, e);
            }
            if(isr != null) try{ isr.close(); }catch(IOException e){
                LOG.log(Level.WARNING, null, e);
            }
            if(fis != null) try{ fis.close(); }catch(IOException e){
                LOG.log(Level.WARNING, null, e);
            }
        }
    }

    public CharSequence readChars(InputStream in) throws IOException {
        return readChars(in, -1);
    }
    
    public CharSequence readChars(InputStream in, int sourceLength) throws IOException {
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try{
            if(charset == null) {
                isr = new InputStreamReader(in);
            }else{
                isr = new InputStreamReader(in, charset);
            }
            reader = new BufferedReader(isr);
            if(sourceLength < 1) {
                return readChars(reader);
            }else{
                return readChars(reader, sourceLength);
            }
        }finally{
            try{ if(isr != null) isr.close(); }
            catch(IOException e) { LOG.log(Level.WARNING, null, e); }
            try{ if(reader != null) reader.close(); }
            catch(IOException e) { LOG.log(Level.WARNING, null, e); }
        }
    }

    /**
     * The reader is closed by this method after the readChars operation
     */
    public CharSequence readChars(Reader reader) throws IOException {
        
        this.checkSum = 0L;
        
        char[] buff = new char[bufferSize];
        
        int nRead;
        
        StringBuilder output = new StringBuilder();
        
        while ( (nRead=reader.read( buff, 0, bufferSize )) != -1 ) {
            
            output.append(buff, 0, nRead);
            
            if(this.isComputeCheckSum()) {
                for ( int i=0; i<nRead; i++ ) {
                    checkSum += buff[i];       
                }    
            }
        }    
        return output;
    }

    /**
     * The reader is closed by this method after the readChars operation
     */
    public CharSequence readChars(Reader reader, int sourceLength) throws IOException {

        CharBuffer buff = CharBuffer.allocate(sourceLength);

        try {

            int read = reader.read(buff);

            buff.flip();
            
            LOG.log(Level.FINE, "Read: {0}", read);
        }finally{
            if(reader != null) try{ reader.close(); }catch(IOException e){
                LOG.log(Level.WARNING, "", e);
            }
        }

        return buff;
    }

    public long getCheckSum() {
        return checkSum;
    }

    public boolean isComputeCheckSum() {
        return computeCheckSum;
    }

    public void setComputeCheckSum(boolean computeCheckSum) {
        this.computeCheckSum = computeCheckSum;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
