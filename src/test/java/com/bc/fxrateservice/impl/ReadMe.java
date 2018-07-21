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

import com.bc.fxrateservice.FxRate;
import com.bc.fxrateservice.FxRateService;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 21, 2018 4:34:23 PM
 */
public class ReadMe {

    public static void main(String... args) {
        
        FxRateService fxRateSvc = new DefaultFxRateService();
       
        FxRate rate = fxRateSvc.getRate(Locale.CHINESE, Locale.ITALIAN);
        
        System.out.println("Rate: " + rate.getRateOrDefault(-1f) + ", date: " + rate.getDate());
        
        rate = fxRateSvc.getRate("USD", "GBP");
        
        System.out.println("Rate: " + rate.getRateOrDefault(-1f) + ", date: " + rate.getDate());
        
        // You could configure update interval
        //
        
        final long updateInterval = TimeUnit.HOURS.toMillis(3);
        
        fxRateSvc = new DefaultFxRateService(
                new ServiceDescriptorImpl("My Forex Rate Service"),
                new FixerFxRateService(updateInterval), 
                new ECBFxRateService(updateInterval)
        );
    }
}
