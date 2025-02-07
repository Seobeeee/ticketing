package com.example.cache;

import com.example.cache.domain.entity.User;
import com.example.cache.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
@RequiredArgsConstructor
public class CacheApplication implements ApplicationRunner {
	private final UserRepository userRepository;
	public static void main(String[] args) {
		SpringApplication.run(CacheApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		userRepository.save(User.builder().name("123").email("123@test.co.kr").build());
		userRepository.save(User.builder().name("456").email("456@test.co.kr").build());
		userRepository.save(User.builder().name("789").email("789@test.co.kr").build());
	}
}
