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
 * @author Chinomso Bassey Ikwuagwu on Mar 20, 2018 8:54:35 PM
 */
public class ECBFxRateService extends FxRateServiceImpl {

    private static final String ENDPOINT = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private static final Date versionDate;
    static{
        final Calendar cal = Calendar.getInstance();
        cal.set(2018, 2, 21, 21, 22, 0);
        versionDate = cal.getTime();
    }

    public ECBFxRateService() {
        super(
                new ServiceDescriptorImpl(
                        "European Central Bank FX Rate Service", 
                        "http://www.ecb.europa.eu", 
                        versionDate, 
                        "1.0"
                ), 
                new SingleEndpointSupplier(ENDPOINT), 
                new ECBUrlParser(), 
                TimeUnit.HOURS.toMillis(24)
        );
    }
}