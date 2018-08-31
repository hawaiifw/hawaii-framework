package org.hawaiiframework.logging.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;

public class KibanaLogFieldsTest {
    @Test
    public void testMdcKeysShouldContainAllMdcKeysExceptLogType() {
        for (final KibanaLogFieldNames fieldName : KibanaLogFieldNames.values()) {
            KibanaLogFields.set(fieldName, "test");
        }

        for (final KibanaLogFieldNames mdcKey : KibanaLogFieldNames.values()) {
            if (mdcKey.equals(KibanaLogFieldNames.LOG_TYPE)) {
                assertThat(String.format("Kibana log string should not contain %s", mdcKey.getLogName()), KibanaLogFields.getValuesAsLogString(), not(containsString(mdcKey.getLogName())));
            } else {
                assertThat(String.format("Kibana log string should contain %s", mdcKey.getLogName()), KibanaLogFields.getValuesAsLogString(), containsString(mdcKey.getLogName()));
            }
        }
    }
}
