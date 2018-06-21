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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.bc.fxrateservice.FxDataFromBaseCurrency;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 10:33:39 PM
 */
public class FixerResponseJson implements FxDataFromBaseCurrency, Serializable {

    private static final Logger LOG = Logger.getLogger(FixerResponseJson.class.getName());

    private final boolean success;
    private final long timestamp;
    private final String baseCurrencyCode;
    private final Date date;
    private final JSONObject rates;

    public FixerResponseJson(String raw) {
        JSONObject json = (JSONObject)JSONValue.parse(raw);
        if(json == null) {
            json = new JSONObject();
        }
        this.success = (Boolean)json.getOrDefault("success", Boolean.FALSE);
        this.timestamp = (Long)json.getOrDefault("timestamp", System.currentTimeMillis());
        this.baseCurrencyCode = (String)json.getOrDefault("base", null);
        Date d;
        try{
            d = new SimpleDateFormat("yyyy-MM-dd").parse((String)json.getOrDefault("date", new Date()));
        }catch(ParseException e) {
            d = new Date();
        }
        this.date = d;
        this.rates = (JSONObject)json.getOrDefault("rates", new JSONObject());

        LOG.info(() -> "Rates:: " + this.rates);
    }
    @Override
    public Float getRate(String toCode, Float outputIfNone) {
        return ((Number)this.rates.getOrDefault(toCode, outputIfNone)).floatValue();
    }
    public boolean isSuccess() {
        return success;
    }
    public long getTimestamp() {
        return timestamp;
    }
    @Override
    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }
    @Override
    public Date getDate() {
        return date;
    }
    public JSONObject getRates() {
        return rates;
    }
}
