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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Function;
import java.util.logging.Logger;
import com.bc.fxrateservice.FxDataFromBaseCurrency;
import com.bc.fxrateservice.FxRate;
import com.bc.fxrateservice.util.GetUrlContent;
import com.bc.fxrateservice.util.UrlReader;
import java.util.concurrent.TimeUnit;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2018 8:20:14 PM
 */
public class UrlParserImpl extends AbstractUrlParser<FxDataFromBaseCurrency> {
    
    private static final Logger LOG = Logger.getLogger(UrlParserImpl.class.getName());

    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL32;
    
    public static final long DATA_REFRESH_INTERVAL = TimeUnit.MINUTES.toMillis(60);
    
    public UrlParserImpl(Function<String, FxDataFromBaseCurrency> converter) {
        this(new GetUrlContent(), converter, DATA_REFRESH_INTERVAL);
    }
    
    public UrlParserImpl(UrlReader urlReader,
            Function<String, FxDataFromBaseCurrency> converter, long dataTimeoutMillis) {
        super(urlReader, converter, dataTimeoutMillis);
    }

    @Override
    public FxRate parseData(
            String fromCode, String toCode, FxDataFromBaseCurrency data) {

        final FxRate output;

        final Float NONE = -1.0f;

        final Float base_toCode_rate = data.getRate(toCode, NONE);

        final String BASE_CURR = data.getBaseCurrencyCode();

        if(BASE_CURR.equalsIgnoreCase(fromCode)) {

            if(base_toCode_rate.equals(NONE)) {
                output = FxRate.NONE;
            }else{
                output = new FxRateImpl(
                    data.getDate(), fromCode, toCode, base_toCode_rate);
            }
        }else{

            final Float base_fromCode_rate = data.getRate(fromCode, NONE);

            if(base_fromCode_rate.equals(NONE) || base_toCode_rate.equals(NONE)) {
                output = FxRate.NONE;
            }else{
                
                final float rate = new BigDecimal(base_toCode_rate, MATH_CONTEXT)
                        .divide(new BigDecimal(base_fromCode_rate, MATH_CONTEXT), MATH_CONTEXT).floatValue();
                
                LOG.finer(() -> base_toCode_rate + " DIVIDE BY " + base_fromCode_rate + " = " + rate);
                
                output = new FxRateImpl(data.getDate(), fromCode, toCode, rate);
            }
        }
        
        LOG.finer(() -> output.toString());

        return output;
    }
}

