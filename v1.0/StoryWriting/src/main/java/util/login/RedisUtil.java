//
// Copyright (c) 2019 山东大学学生在线. All rights reserved.
//

package util.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis访问工具
 *
 * @author Zsj
 */
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public RedisUtil() {

    }

    public String get(String key) {
        if (stringRedisTemplate.hasKey(key)) {
            return stringRedisTemplate.opsForValue().get(key);
        }
        return "";
    }

    public void set(String key, String value, long expire) {
        stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.MILLISECONDS);
    }

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public StringRedisTemplate getTemplate() {
        return stringRedisTemplate;
    }
}
