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

import com.bc.fxrateservice.ServiceDescriptor;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 9:06:57 PM
 */
public class ServiceDescriptorImpl implements ServiceDescriptor, Serializable {
    
    private final String name;
    private final String vendor;
    private final Date versionDate;
    private final String version;

    public ServiceDescriptorImpl(String name, String vendor, Date versionDate, String version) {
        this.name = Objects.requireNonNull(name);
        this.vendor = Objects.requireNonNull(vendor);
        this.versionDate = Objects.requireNonNull(versionDate);
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public String getId() {
        return vendor.replace(' ', '_') + ':' + name.replace(' ', '_') + ':' + version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVendor() {
        return vendor;
    }

    @Override
    public Date getVersionDate() {
        return versionDate;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.vendor);
        hash = 89 * hash + Objects.hashCode(this.versionDate);
        hash = 89 * hash + Objects.hashCode(this.version);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServiceDescriptorImpl other = (ServiceDescriptorImpl) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.vendor, other.vendor)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.versionDate, other.versionDate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' + this.getId() + '}';
    }
}
