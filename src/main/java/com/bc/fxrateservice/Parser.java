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

package com.bc.fxrateservice;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 5:26:24 PM
 * @param <SOURCE_TYPE>
 */
public interface Parser<SOURCE_TYPE> {

    FxRate parse(String fromCode, String toCode, SOURCE_TYPE source);

    FxRate[] parse(String fromCode, String[] toCodes, SOURCE_TYPE source);
}
