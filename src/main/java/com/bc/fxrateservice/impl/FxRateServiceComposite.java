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
import com.bc.fxrateservice.ServiceDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 27, 2018 2:13:12 PM
 */
public class FxRateServiceComposite implements FxRateService {

    private static final Logger LOG = Logger.getLogger(FxRateServiceComposite.class.getName());

    private final List<FxRateService> serviceList;
    
    private final ServiceDescriptor descriptor;

    private FxRateService activeFxRateService;
    
    public FxRateServiceComposite(ServiceDescriptor descriptor, 
            FxRateService preferred, FxRateService... fallbacks) {
        final List<FxRateService> list = new ArrayList();
        list.add(preferred);
        if(fallbacks != null && fallbacks.length > 0) {
            list.addAll(Arrays.asList(fallbacks));
        }
        this.serviceList = Collections.unmodifiableList(list);
        if(serviceList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.activeFxRateService = serviceList.get(0);
        this.descriptor = Objects.requireNonNull(descriptor);
    }
    
    @Override
    public ServiceDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public FxRate getRate(Locale fromLocale, Locale toLocale) {
        return (FxRate)this.execute("getRate", new Class[]{Locale.class, Locale.class}, fromLocale, toLocale);
    }

    @Override
    public FxRate getRate(String fromCode, String toCode) {
        return (FxRate)this.execute("getRate", new Class[]{String.class, String.class}, fromCode, toCode);
    }

    @Override
    public FxRate loadRate(String fromCode, String toCode) {
        return (FxRate)this.execute("loadRate", new Class[]{String.class, String.class}, fromCode, toCode);
    }

    @Override
    public void clearCache() {
        for(FxRateService fxSvc : serviceList) {
            fxSvc.clearCache();
        }
    }

    @Override
    public long getUpdateIntervalMillis(long outputIfNone) {
        final FxRateService fxSvc;
        if(activeFxRateService == null) {
            if(serviceList.isEmpty()) {
                fxSvc = null;
            }else{
                fxSvc = serviceList.get(0);
            }
        }else{
            fxSvc = activeFxRateService;
        }
        return fxSvc == null ? outputIfNone : fxSvc.getUpdateIntervalMillis(outputIfNone);
    }
    
    public FxRate execute(String methodName, Class<?> [] parameterTypes, Object... methodArgs) {
        FxRate fxRate = FxRate.NONE;
        for(FxRateService fxSvc : serviceList) {
            try{
                if(parameterTypes==null || parameterTypes.length == 0 ||
                        methodArgs == null || methodArgs.length == 0) {
                    fxRate = (FxRate)fxSvc.getClass().getMethod(methodName).invoke(fxSvc);
                }else{
                    fxRate = (FxRate)fxSvc.getClass().getMethod(methodName, parameterTypes).invoke(fxSvc, methodArgs);
                }
            }catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LOG.log(Level.WARNING, "{0}", e.toString());
                LOG.log(Level.FINE, null, e);
            }
            if(fxRate != null && fxRate != FxRate.NONE) {
                activeFxRateService = fxSvc;
                break;
            }
        }        
        
        return fxRate;
    }

    public List<FxRateService> getServiceList() {
        return Collections.unmodifiableList(serviceList);
    }

    public FxRateService getActiveFxRateService() {
        return activeFxRateService;
    }

    @Override
    public String toString() {
        return descriptor.toString();
    }
}
