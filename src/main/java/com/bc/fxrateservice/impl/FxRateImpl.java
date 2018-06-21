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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import com.bc.fxrateservice.FxRate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 2:53:09 PM
 */
public class FxRateImpl implements FxRate, Serializable {

    private final Date date;

    private final String fromCode;
    
    private final String toCode;
    
    private final float rate;
    
    public FxRateImpl(Date date, String fromCode, String toCode, float rate) {
        this.date = Objects.requireNonNull(date);
        this.fromCode = Objects.requireNonNull(fromCode);
        this.toCode = Objects.requireNonNull(toCode);
        this.rate = rate;
    }

    @Override
    public String getFromCode() {
        return fromCode;
    }

    @Override
    public String getToCode() {
        return toCode;
    }

    @Override
    public float getRate() {
        return rate;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.date);
        hash = 79 * hash + Objects.hashCode(this.fromCode);
        hash = 79 * hash + Objects.hashCode(this.toCode);
        hash = 79 * hash + Float.floatToIntBits(this.rate);
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
        final FxRateImpl other = (FxRateImpl) obj;
        if (Float.floatToIntBits(this.rate) != Float.floatToIntBits(other.rate)) {
            return false;
        }
        if (!Objects.equals(this.fromCode, other.fromCode)) {
            return false;
        }
        if (!Objects.equals(this.toCode, other.toCode)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' + fromCode + ':' + toCode + "=" + rate + 
                " on " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(date) + '}';
    }
}
