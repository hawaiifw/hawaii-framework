package org.hawaiiframework.boot.autoconfigure.cache;

import org.hawaiiframework.boot.autoconfigure.cache.redis.HawaiiRedisAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.JedisPoolConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HawaiiRedisAutoConfiguration.class})
public class HawaiiRedisPoolConfigurationTest {

    @MockBean
    JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private JedisPoolConfig jedisPoolConfig;

    @Test
    public void assertRedisWiring() {
        assertNotNull(jedisPoolConfig);
        assertEquals(jedisPoolConfig.getMinEvictableIdleTimeMillis(), 30000);
    }
}
