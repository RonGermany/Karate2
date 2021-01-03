package com.intuit.karate.report;

import com.intuit.karate.core.Feature;
import com.intuit.karate.core.FeatureRuntime;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pthomas3
 */
class ReportTest {

    static final Logger logger = LoggerFactory.getLogger(ReportTest.class);

    @Test
    void testReport() {
        Feature feature = Feature.read("classpath:com/intuit/karate/report/test.feature");
        FeatureRuntime fr = FeatureRuntime.of(feature);
        fr.run();
        ReportUtils.saveHtmlFeatureReport(fr.result, "target/report-test");
    }

}
