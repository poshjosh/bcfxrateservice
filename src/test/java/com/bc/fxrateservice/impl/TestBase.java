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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 4, 2018 10:16:45 PM
 */
public class TestBase {
    static{
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try(InputStream in = cl.getResourceAsStream("META-INF/logging.properties")) {
            LogManager.getLogManager().readConfiguration(in);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
