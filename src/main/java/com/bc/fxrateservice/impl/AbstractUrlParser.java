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
package com.bc.fxrateservice.impl;

import java.util.Objects;
import java.util.function.Function;
import com.bc.fxrateservice.Parser;
import java.util.logging.Logger;
import com.bc.fxrateservice.FxRate;
import com.bc.fxrateservice.util.UrlReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 8:30:38 PM
 * @param <DATA_TYPE>
 */
public abstract class AbstractUrlParser<DATA_TYPE> implements Parser<String> {
    
    private static final Logger LOG = Logger.getLogger(AbstractUrlParser.class.getName());

    private final Function<String, DATA_TYPE> converter;
    
    private final long dataTimeoutMillis;

    private long dataDownloadTime;
    
    private final UrlReader urlReader;

    protected AbstractUrlParser(UrlReader urlReader, 
            Function<String, DATA_TYPE> converter, long dataTimeoutMillis) {
        this.urlReader = Objects.requireNonNull(urlReader);
        this.converter = Objects.requireNonNull(converter);
        this.dataTimeoutMillis = dataTimeoutMillis;
    }

    public abstract FxRate parseData(String fromCode, String toCode, DATA_TYPE data);
    
    @Override
    public FxRate[] parse(String fromCode, String[] toCodes, String url) {
        
        final DATA_TYPE data = this.getData(url, null);
        
        final FxRate [] result = new FxRate[toCodes.length];
        
        if(data == null) {
            Arrays.fill(result, FxRate.NONE);
        }else{
            for(int i=0; i<toCodes.length; i++) {
                result[i] = this.parseData(fromCode, toCodes[i], data);
            }
        }
        
        return result;
    }
    
    @Override
    public FxRate parse(String fromCode, String toCode, String url) {
        
        final DATA_TYPE data = this.getData(url, null);

        final FxRate result;
        
        if(data == null) {
            result = FxRate.NONE;
        }else{
            result = this.parseData(fromCode, toCode, data);
        }
        
        return result;
    }
    
    private DATA_TYPE _d;
    public DATA_TYPE getData(String url, DATA_TYPE outputIfNone) {
        
        boolean dataUpdated = false;
        
        if(_d == null || this.isExpired(_d)) {
            
            try{
                
                final String raw = urlReader.read(url);
                
                if(raw != null) {

                    _d = converter.apply(raw);
                    dataDownloadTime = System.currentTimeMillis();

                    dataUpdated = true;
                }
            }catch(IOException e) {
                LOG.log(Level.WARNING, "Exception loading data from: " + url, e);
            }
        }
        
        if(_d != null && !dataUpdated) {
            LOG.finer(() -> "Re-using existing data");
        }
        
        return _d == null ? outputIfNone : _d;
    }

    public boolean isExpired(DATA_TYPE data) {
        return data == null || ((System.currentTimeMillis() - dataDownloadTime) > dataTimeoutMillis);
    }
}
