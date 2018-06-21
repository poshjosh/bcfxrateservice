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

import com.bc.xml.NodeListUtil;
import com.bc.xml.XmlDom;
import com.bc.xml.XmlUtil;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.bc.fxrateservice.FxDataFromBaseCurrency;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 10:29:21 PM
 */
public class ECBResponseXml extends XmlDom implements FxDataFromBaseCurrency {

    private static final Logger LOG = Logger.getLogger(ECBResponseXml.class.getName());

    public static final String ROOT_NODE = "gesmes:Envelope";
    private final String baseCurrencyCode;
    private final Node ratesParentNode;
    private final Date date;

    public ECBResponseXml(String raw) {
        this(new InputSource(new StringReader(Objects.requireNonNull(raw))));
    }

    public ECBResponseXml(InputSource inputSource) {
        super(inputSource, ROOT_NODE, false);
        this.baseCurrencyCode = "EUR";
        this.ratesParentNode = this.loadRatesParentNode().orElse(null);
        
        LOG.fine(() -> "Rates parent node: " + this.ratesParentNode);
        
        if(ratesParentNode == null) {
            this.date = new Date();
        }else{
            // 2018-03-20
            final String timeStr = new XmlUtil().getAttributeValue(ratesParentNode, "time");
            Objects.requireNonNull(timeStr);
            Date date;
            try{
                date = new SimpleDateFormat("yyyy-MM-dd").parse(timeStr);
            }catch(ParseException e) {
                LOG.log(Level.WARNING, "Exception parsing: " + timeStr, e);
                date = new Date();
            }
            this.date = date == null ? new Date() : date;
        }
        LOG.fine(() -> "Rates date: " + date);
    }

    private Optional<Node> loadRatesParentNode() {
        final NodeList cubeList = this.getDocument().getElementsByTagName("Cube");
        return new NodeListUtil(cubeList).getNodeWithMostChildren();
    }

    @Override
    public Float getRate(String currCode, Float outputIfNone) {
        
        final String rateStr = this.getRateString(currCode, null);
        
        final Number output = rateStr == null ? outputIfNone : new BigDecimal(rateStr, UrlParserImpl.MATH_CONTEXT);
        
        return output == null ? outputIfNone : output.floatValue();
    }

    public String getRateString(String currCode, String outputIfNone) {

        if(ratesParentNode ==  null) {

            return outputIfNone;

        }else{

            final NodeList cubes = ratesParentNode.getChildNodes();

            if(cubes == null || cubes.getLength() == 0) {

                return outputIfNone;

            }else{

                return getRateString(cubes, currCode, outputIfNone);
            }
        }    
    }

    private String getRateString(NodeList cubes, String currCode, String outputIfNone) {

        final int cubesLen = cubes.getLength();

        final XmlUtil xml = new XmlUtil();

        for(int i = 0; i<cubesLen; i++) {

            final Node cube = cubes.item(i);
            
            LOG.finer(xml.toString(cube));

            String code = xml.getAttributeValue(cube, "currency");

            if(code != null) {
                code = code.trim().toLowerCase();
            }

            if(currCode.trim().toLowerCase().equals(code)) {

                return xml.getAttributeValue(cube, "rate");
            }
        }

        return outputIfNone;
    }
    
    @Override
    public String getBaseCurrencyCode() {
        return this.baseCurrencyCode;
    }

    public Node getRatesParentNode() {
        return ratesParentNode;
    }

    @Override
    public Date getDate() {
        return date;
    }
}
