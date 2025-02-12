package com.example.WebfluxStudy.serivce;


import com.example.WebfluxStudy.repository.User;
import com.example.WebfluxStudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public Mono<User> create(String name, String email){
        return userRepository.save(User.builder().name(name).email(email).build());
    }

    public Flux<User> findAll(){
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id){
        return userRepository.findById(id);
    }

    public Mono<Integer> deleteById(Long id){
        return userRepository.deleteById(id);
    }

    public Mono<User> update(Long id, String name, String email){
        return userRepository.findById(id)
                .flatMap(u -> {
                    u.setName(name);
                    u.setEmail(email);
                    return userRepository.save(u);
                });
    }
}
