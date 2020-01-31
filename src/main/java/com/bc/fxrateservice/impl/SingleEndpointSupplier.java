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

import com.bc.fxrateservice.EndpointSupplier;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 8:01:15 PM
 */
public class SingleEndpointSupplier implements EndpointSupplier, Serializable {

    private transient static final Logger LOG = Logger.getLogger(SingleEndpointSupplier.class.getName());

    private final String endpoint;

    public SingleEndpointSupplier(String endpoint) {
        this.endpoint = Objects.requireNonNull(endpoint);
        LOG.log(Level.FINE, "Endpoint: {0}", endpoint);                   
    }
    
    @Override
    public String get(String fromCode, String toCode) {
        return endpoint;
    }
    
    @Override
    public String get(String fromCode, String[] toCodes) {
        return endpoint;
    }
}
