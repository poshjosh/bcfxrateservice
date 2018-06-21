package com.bc.fxrateservice.impl;

import com.bc.fxrateservice.EndpointSupplier;
import com.bc.fxrateservice.util.GetUrlContents;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.fxrateservice.Parser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import com.bc.fxrateservice.FxRate;

/**
 * @(#)YahooCurrencyrateService.java   20-Oct-2014 09:59:29
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * End point http://finance.yahoo.com/d/quotes.csv?s=%s=X&f=l1d1t1&e=.csv
 * %s should be replaced by the concantenation of FROM_CODE and TO_CODE
 * http://www.gummy-stuff.org/Yahoo-data.htm
 * @deprecated Yahoo no longer supports. Use {@link com.bc.currencyrateservice.CurrencyrateService#getAvailable()}
 * @see https://stackoverflow.com/questions/4280761/how-do-i-create-an-org-xml-sax-inputsource-from-a-string
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
@Deprecated
public class YahooFxRateService extends FxRateServiceImpl {

    private static final Logger LOG = Logger.getLogger(YahooFxRateService.class.getName());
    
    public static class EndpointSupplierImpl implements EndpointSupplier {
        @Override
        public String get(String fromCode, String toCode) {
            return this.getUrlPart()+this.getKey(fromCode, toCode)+"=X";
        }
        @Override
        public String get(String[] fromCodes, String[] toCodes) {
            if(fromCodes.length != toCodes.length) {
                throw new IllegalArgumentException();
            }
            StringBuilder builder = new StringBuilder();
            builder.append(this.getUrlPart());
            for(int i=0; i<fromCodes.length; i++) {
                builder.append(this.getKey(fromCodes[i], toCodes[i])).append("=X");
                if(i < fromCodes.length -1) {
                    builder.append(',');
                }
            }
            return builder.toString();
        }
        public String getUrlPart() {
            return " http://finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=";
    //        return "http://download.finance.yahoo.com/d/quotes.csv?f=nl1d1t1&s=";
    //        return "http://download.finance.yahoo.com/d/quotes.csv?f=l1d1t1&e=.csv&s=";
    //        return "http://finance.yahoo.com/d/quotes.csv?f=l1d1t1&e=.csv&s=";
        }
        public String getKey(final String fromCode, final String toCode) {
            return fromCode.toUpperCase()+toCode.toUpperCase();
        }
    }
    
    public static class ResponseParserImpl implements Parser<String> {
        
        @Override
        public FxRate[] parse(String[] fromCodes, String[] toCodes, String url) {
            if(fromCodes.length != toCodes.length) {
                throw new IllegalArgumentException();
            }
            try(
                    InputStream in = new URL(url).openStream();
                    InputStreamReader r = new InputStreamReader(in, "utf-8");
                    BufferedReader br = new BufferedReader(r)
            ){
                FxRate [] rates = new FxRate[fromCodes.length];
                String line;
                for(int i=0; ((line = br.readLine()) != null); i++) {
                    line = line.trim();
                    if(line.isEmpty()) {
                        continue;
                    }
                    rates[i] = this.parseResponse(fromCodes[i], toCodes[i], line);
                }
                return rates;
            }catch(IOException e) {
                LOG.log(Level.WARNING, "", e);
                return new FxRate[0];
            }
        }
        
        @Override
        public FxRate parse(
                final String fromCode, final String toCode, String url) {

    //Format: 
    //203.8524,"3/26/2013","7:10pm"
    //0.1589,"12/28/2015","1:15pm"
    //N/A,N/A,N/A
    //4.7470,"12/28/2015","1:15pm"
    //0.0002,"12/28/2015","1:15pm"
    
            final String rawResponse = new GetUrlContents().apply(url);
            
            return this.parseResponse(fromCode, toCode, rawResponse);
        }    

        public FxRate parseResponse(
                final String fromCode, final String toCode, String rawResponse) {

            final String [] parts = rawResponse.split(",");

            if(parts.length < 1) {

                LOG.log(Level.WARNING, "Invalid endpoint response {0}", rawResponse);           

                return FxRate.NONE;

            }else{

                LOG.log(Level.FINER, "Exchange rate: {0}", parts[0]);                    
            }

            // Update the last known mRate with our new value
            float rate;
            try{
                rate = Float.parseFloat(parts[0]) ;
            }catch(NumberFormatException e) {
                return FxRate.NONE;
            }

            final Date date = extractDate(parts, rawResponse);

            return new FxRateImpl(date, fromCode, toCode, rate);
        }

        private Date extractDate(String [] parts, String raw) {

            if(parts.length > 1) {

                parts[1] = parts[1].trim(); // Very important

                LOG.log(Level.FINER, "Date part: {0}", parts[1]);                    

                // Remove the opening and closing "
                String dateStr = parts[1].substring(1, parts[1].length()-1);

                String timeStr = null;

                if(parts.length > 2) {

                    parts[2] = parts[2].trim(); // Very important

                    LOG.log(Level.FINER, "Time part: {0}", parts[2]);                    

                    // Remove the opening and closing "
                    timeStr = parts[2].substring(1, parts[2].length()-1);
                }

                return parseDate(dateStr, timeStr);

            }else{

                LOG.log(Level.WARNING, 
                "Using current date. Reason: Invalid date/time part contained in output: {0}", raw); 

                return new Date();
            }
        }

        /**
         * @param dateStr Date String.  Expected SHORT mDate format e.g 3/26/2013
         * @param timeStr Time String. Expected SHORT time format e.g 7:10pm
         * @return A Date object
         */
        private Date parseDate(String dateStr, String timeStr) {
            final SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("MM/dd/yyyy hh:mma");
            Date date;
            try{
                StringBuilder builder = new StringBuilder(dateStr);
                if(timeStr != null) {
                    builder.append(' ').append(timeStr);
                }
                date = sdf.parse(builder.toString());
            }catch(ParseException e) {
                date = new Date();
                LOG.log(Level.WARNING, 
                "Failed to parse date from response. Will use current date: "+date, e);
            }
            return date;
        }
    }

    private static final Date versionDate;
    static{
        final Calendar cal = Calendar.getInstance();
        cal.set(2015, 11, 28, 12, 35, 00);
        versionDate = cal.getTime();
    }

    public YahooFxRateService() {
        this(TimeUnit.HOURS.toMillis(1));
    }

    public YahooFxRateService(long updateInterval) {
        super(
                new ServiceDescriptorImpl(
                        "Yahoo FX Rate Service", "Yahoo", versionDate, "1.1"
                ), 
                new EndpointSupplierImpl(), 
                new ResponseParserImpl(), 
                updateInterval
        );
    }
}
