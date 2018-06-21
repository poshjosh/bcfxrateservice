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
import java.net.URL;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 5:12:07 PM
 */
public class GetUrlContents extends CharReader implements Function<String, String> {

    private static final Logger LOG = Logger.getLogger(GetUrlContents.class.getName());

    @Override
    public String apply(String urlStr) {
        
        LOG.log(Level.FINE, "URL to read: {0}", urlStr);                   

        InputStream in = null;
        
        try{
            
            final URL url = new URL(urlStr);   
            
            in = url.openStream();
            
            final CharSequence cs = this.readChars(in);
            
            LOG.log(Level.FINER, "Endpoint response: {0}", cs);                   
          
            return cs.toString();
            
        }catch(IOException e) {
            
            LOG.log(Level.WARNING, "Error reading: "+urlStr, e);
            
            return null;
            
        }finally{
            try{ 
                if(in != null) {
                    in.close();
                } 
            }catch(IOException e) { 
                LOG.log(Level.WARNING, "Failed to close input stream", e); 
            }
        }
    }
    
}
