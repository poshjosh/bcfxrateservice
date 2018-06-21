package com.bc.fxrateservice;

import com.bc.fxrateservice.impl.FxRateImpl;
import java.io.Serializable;
import java.util.Date;

/**
 * @(#)Currencyrate.java   20-Oct-2014 09:31:13
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface FxRate extends Serializable {
    
    FxRate NONE = new FxRateImpl(new Date(), "", "", 0.0f);
    
    String getFromCode();
    String getToCode();
    default float getRateOrDefault(float outputIfNone) {
        float rate = this.getRate();
        return rate <= 0.0f ? outputIfNone : rate;
    }
    float getRate();
    Date getDate();
}
