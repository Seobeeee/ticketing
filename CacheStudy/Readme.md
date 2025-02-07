# SpringBoot Cache 관련 학습 프로젝트
오랜만에 사용하는 스프링 부트에 대하여 감을 잃지 않기 위하여, 기존에 사용하던 부분을 복습 겸 만든 프로젝트입니다.
프로젝트를 진행하면서 놓쳤던 부분들이나, Config 설정하는 부분들을 정리하기 위하여 Readme를 생성합니다.

---
## RedisCacheConfig 설정
기존에 Redis에 객체를 넣을 경우, 객체가 Json 형식으로 들어가야 하는데, 이 부분을 매번 구글링해서 찾아 프로젝트에 적용하였는데,
이번에 아래와 같은 방법으로 Config를 설정하여 보다 용이하게 다른 프로젝트에서도 사용할 수 있을 것으로 보아
정리하였습니다.

```java
@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<String, User> userRedisTemplate(RedisConnectionFactory connectionFactory){
        var objectMapper = new ObjectMapper()
                // Deserialization 과정에서 모르는 값이 있으면 무효처리, 해당 설정을 진행하지 않을 경우 실패 처리 됨.
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        var template = new RedisTemplate<String, User>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, User.class));
        return template;
    }
}
```
위의 소스처럼 진행 할 경우, User 객체에 대한 부분만 처리되기 때문에 객체가 생성될 때 마다 Template을 생성해주어야 하는 번거로움이 존재
따라서, 해당 부분을 Object 단위로 사용할 수 있게 Template을 만들어 주어야 제너럴하게 사용할 수 있을것으로 보임
해당 부분은 아래의 소스로 처리합니다.
```Java
    @Bean
    RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory connectionFactory){
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
                .builder()
                .allowIfSubType(Object.class)
                .build();

        var objectMapper = new ObjectMapper()
                // Deserialization 과정에서 모르는 값이 있으면 무효처리, 해당 설정을 진행하지 않을 경우 실패 처리 됨.
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                // 클래스 정보를 같이 저장하여 Deserialize할 때 에러가 나지 않게 하기 위함.
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }
```
