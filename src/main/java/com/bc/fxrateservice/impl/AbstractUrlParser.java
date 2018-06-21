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
import com.bc.fxrateservice.util.GetUrlContents;
import java.util.logging.Logger;
import com.bc.fxrateservice.FxRate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 8:30:38 PM
 * @param <DATA_TYPE>
 */
public abstract class AbstractUrlParser<DATA_TYPE> implements Parser<String> {
    
    private static final Logger LOG = Logger.getLogger(AbstractUrlParser.class.getName());

    private DATA_TYPE data;
    
    private final Function<String, DATA_TYPE> converter;

    protected AbstractUrlParser(Function<String, DATA_TYPE> converter) {
        this.converter = Objects.requireNonNull(converter);
    }

    public abstract FxRate parseData(String fromCode, String toCode, DATA_TYPE data);
    
    @Override
    public FxRate[] parse(String[] fromCodes, String[] toCodes, String url) {
        final int len = Math.min(fromCodes.length, toCodes.length);
        final FxRate [] output = new FxRate[len];
        for(int i=0; i<len; i++) {
            output[i] = this.parse(fromCodes[i], toCodes[i], url);
        }
        return output;
    }
    
    @Override
    public FxRate parse(String fromCode, String toCode, String url) {
        
        boolean dataUpdated = false;
        
        if(data == null || this.isExpired(data)) {
            
            final String raw = new GetUrlContents().apply(url);
            
            if(raw != null) {
                
                data = converter.apply(raw);
                
                dataUpdated = true;
            }
        }
        
        if(data == null) {
            return FxRate.NONE;
        }else{
            if(!dataUpdated) {
                LOG.fine(() -> "Re-using existing data");
            }
            return this.parseData(fromCode, toCode, data);
        }
    }
    
    public boolean isExpired() {
        return this.isExpired(data);
    }
    
    public boolean isExpired(DATA_TYPE data) {
        return data == null;
    }
}
