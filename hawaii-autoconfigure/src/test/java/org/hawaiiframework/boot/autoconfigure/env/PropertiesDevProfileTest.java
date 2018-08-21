package org.hawaiiframework.boot.autoconfigure.env;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PropertiesDevProfileTest.class})
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles(profiles = "dev")
public class PropertiesDevProfileTest extends PropertiesDefaultProfileTestBase {

    private static final Map<String, Object> DEFAULT_HAWAII_DEV_PROPERTIES;

    @Autowired
    private ConfigurableApplicationContext context;

    static {
        DEFAULT_HAWAII_DEV_PROPERTIES = new HashMap<>(DEFAULT_HAWAII_PROPERTIES);
        DEFAULT_HAWAII_DEV_PROPERTIES.put("logging.level.org.hawaiiframework", "DEBUG");
        DEFAULT_HAWAII_DEV_PROPERTIES.put("spring.jackson.serialization.indent-output", "true");
    }

    @Test
    public void testDefaultHawaiiPropertiesDevProfile() {
        Map<String, Object> expected = new HashMap<>(DEFAULT_HAWAII_DEV_PROPERTIES);
        expected.forEach((key, value) -> {
            assertThat(getEnvProperty(key), is(equalTo(value)));
        });
    }

    private String getEnvProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }
}
