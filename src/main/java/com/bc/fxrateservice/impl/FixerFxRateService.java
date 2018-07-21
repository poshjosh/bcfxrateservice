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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 7:54:19 PM
 */
public class FixerFxRateService extends FxRateServiceImpl {

    private static final String ACCESS_KEY = "4ff12d44cf6838967be063ac4fc7c4f4";
    private static final String ENDPOINT = "http://data.fixer.io/api/latest?access_key=" + ACCESS_KEY;
    
    private static final Date versionDate;
    static{
        final Calendar cal = Calendar.getInstance();
        cal.set(2018, 2, 21, 21, 22, 0);
        versionDate = cal.getTime();
    }

    public FixerFxRateService() {
        this(TimeUnit.HOURS.toMillis(24));
    }
    
    public FixerFxRateService(long updateIntervalMillis) {
        super(
                new ServiceDescriptorImpl(
                        "Fixer FX Rate Service", 
                        "https://fixer.io", 
                        versionDate, 
                        "1.0"
                ), 
                new SingleEndpointSupplier(ENDPOINT), 
                new FixerUrlParser(), 
                updateIntervalMillis
        );
    }
}
