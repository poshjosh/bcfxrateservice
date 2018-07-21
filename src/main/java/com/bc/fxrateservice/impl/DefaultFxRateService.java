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

import com.bc.fxrateservice.FxRateService;
import com.bc.fxrateservice.ServiceDescriptor;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 27, 2018 2:51:27 PM
 */
public class DefaultFxRateService extends FxRateServiceComposite {

    private static final Date versionDate;
    static{
        final Calendar cal = Calendar.getInstance();
        cal.set(2018, 2, 27, 14, 50, 0);
        versionDate = cal.getTime();
    }
    
    public DefaultFxRateService() {
        super(
                new ServiceDescriptorImpl(
                        "All available Fx Rate Services", 
                        "All available vendors", 
                        versionDate, 
                        "1.0"
                ),
                new FixerFxRateService(),
                new ECBFxRateService()
        );
    }

    public DefaultFxRateService(ServiceDescriptor descriptor, 
            FxRateService preferred, FxRateService... fallbacks) {
        super(descriptor, preferred, fallbacks);
    }
}
