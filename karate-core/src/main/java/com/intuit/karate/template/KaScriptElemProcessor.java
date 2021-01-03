/*
 * The MIT License
 *
 * Copyright 2020 Intuit Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.intuit.karate.template;

import com.intuit.karate.StringUtils;
import com.intuit.karate.graal.JsEngine;
import com.intuit.karate.http.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author pthomas3
 */
public class KaScriptElemProcessor extends AbstractElementModelProcessor {

    private static final Logger logger = LoggerFactory.getLogger(KaScriptElemProcessor.class);
    
    private final JsEngine jsEngine;

    public KaScriptElemProcessor(String dialectPrefix, JsEngine jsEngine) {
        super(TemplateMode.HTML, dialectPrefix, "script", false, "lang", true, 1000);
        this.jsEngine = jsEngine;
    }

    @Override
    protected void doProcess(ITemplateContext ctx, IModel model, IElementModelStructureHandler sh) {
        int n = model.size();
        boolean isHead = TemplateUtils.hasAncestorElement(ctx, "head");
        IModel headModel = null;
        while (n-- != 0) {
            final ITemplateEvent event = model.get(n);
            if (event instanceof IText) {
                String text = StringUtils.trimToNull(((IText) event).getText());
                if (isHead) {
                    if (text != null) {
                        if (jsEngine != null) {
                            jsEngine.eval(text);
                        } else {
                            JsEngine.evalGlobal(text);
                        }                        
                    }
                    if (headModel == null) {
                        headModel = TemplateUtils.generateHeadScriptTag(ctx);
                    }
                } else if (text != null) {
                    if (jsEngine != null) {
                        jsEngine.eval(text);
                    } else {
                        RequestCycle.get().evalAndQueue(text);
                    }
                }
            }
        }
        model.reset();
        if (headModel != null) {
            model.addModel(headModel);
        }
    }

}
