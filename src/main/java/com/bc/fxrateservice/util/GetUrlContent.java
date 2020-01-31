/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.fxrateservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 5:12:07 PM
 */
public class GetUrlContent<E extends GetUrlContent> implements UrlReader {

    private transient static final Logger LOG = Logger.getLogger(GetUrlContent.class.getName());
    
    private static CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);    
    
    private Map<String, String> headers;
    
    private final String charsetName;
    
    private final int bufferSize;

    public GetUrlContent() { 
        this(StandardCharsets.UTF_8.name(), 1024 * 8);
    }

    public GetUrlContent(String charsetName, int bufferSize) { 
        this.charsetName = Objects.requireNonNull(charsetName);
        this.bufferSize = bufferSize;
    }
    
    public E addHeader(String name, String value) {
        if(headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, value);
        return (E)this;
    }
    
    public String apply(String urlString, String outputIfNone) {
        String result = null;
        try{
            result = this.read(urlString);
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Failed to read: " + urlString, e);
        }
        return result == null ? outputIfNone : result;
    }
    
    @Override
    public String read(String urlStr) throws IOException {
        
        LOG.log(Level.FINE, "URL to read: {0}", urlStr);                   

        final StringBuilder appendResult = new StringBuilder();
        
        try(InputStreamReader reader = new InputStreamReader(
                this.getInputStream(new URL(urlStr)), charsetName)){
            
            this.appendChars(reader, appendResult);
            
            LOG.log(Level.FINER, "Endpoint response: {0}", appendResult);                   
          
        }catch(IOException e) {
            
            LOG.log(Level.WARNING, "Error reading: "+urlStr, e);
        }
        
        return appendResult.toString();
    }
    
    public void appendChars(Reader reader, StringBuilder appendTo) throws IOException {
        
        char[] buff = new char[bufferSize];
        
        int nRead;
        
        while ( (nRead=reader.read( buff, 0, bufferSize )) != -1 ) {
            
            appendTo.append(buff, 0, nRead);
        }
    }
    
    public InputStream getInputStream(URL url) throws IOException {
       
        final CookieHandler defaultCookieHandler = CookieHandler.getDefault();
        try{
            CookieHandler.setDefault(cookieManager);
            final URLConnection conn = url.openConnection();
            if(headers != null && !headers.isEmpty()) {
                final Set<Map.Entry<String, String>> entrySet = headers.entrySet();
                for(Map.Entry<String, String> entry : entrySet) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            HttpURLConnection httpConn = null;
            if(conn instanceof HttpURLConnection) {
                httpConn = (HttpURLConnection)conn;
            }
            if(httpConn != null) {
                httpConn.setInstanceFollowRedirects(true);
            }
            conn.setConnectTimeout(30_000); 
            conn.setReadTimeout(60_000);
            conn.setDoOutput(true);
            conn.setAllowUserInteraction(true);
            conn.setRequestProperty("User-Agent", 
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.A.B.C Safari/525.13");
            final InputStream in;
            if(httpConn == null) {
                in = conn.getInputStream();
            }else{
                final int code = httpConn.getResponseCode();
                if(code < 300) {
                    in = conn.getInputStream();
                }else{
                    final String message = httpConn.getResponseMessage();
                    if(message != null) {
                        LOG.warning(() -> "Response. code: " + code + ", message: " + message);
                    }
                    in = httpConn.getErrorStream();
                }
            }
            return in;
        }finally{
            CookieHandler.setDefault(defaultCookieHandler);
        }
    }
}
