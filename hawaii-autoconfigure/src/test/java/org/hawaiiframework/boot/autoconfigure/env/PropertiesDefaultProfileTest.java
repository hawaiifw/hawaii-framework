package org.hawaiiframework.boot.autoconfigure.env;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PropertiesDefaultProfileTest.class})
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
public class PropertiesDefaultProfileTest extends PropertiesDefaultProfileTestBase {

    @Test
    public void testDefaultHawaiiProperties() {
        Map<String, Object> expected = new HashMap<>(DEFAULT_HAWAII_PROPERTIES);
        expected.forEach((key, value) -> {
            assertThat(getEnvProperty(key), is(equalTo(value)));
        });
    }

    @Test
    public void testCustomBannerLocation() {
        TestPropertyValues
                .of("spring.banner.location: classpath:my-banner.txt")
                .applyTo(context);

        assertThat(getEnvProperty("spring.banner.location"), is(equalTo("classpath:my-banner.txt")));
    }

    private String getEnvProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }
}

