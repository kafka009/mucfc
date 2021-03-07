package com.mucfc.mybatis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MybatisApplication implements CommandLineRunner {
    private final UserMapper userMapper;

    public MybatisApplication(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(MybatisApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println(this.userMapper.findByName("博狗"));
        {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                this.userMapper.findByName("博狗");
            }
            System.out.println(System.currentTimeMillis() - start);
        }
        {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                this.userMapper.findById(1L);
            }
            System.out.println(System.currentTimeMillis() - start);
        }

        {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                this.userMapper.findByName("博狗");
            }
            System.out.println(System.currentTimeMillis() - start);
        }
        {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                this.userMapper.findById(1L);
            }
            System.out.println(System.currentTimeMillis() - start);
        }
        {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                this.userMapper.findByName("博狗");
            }
            System.out.println(System.currentTimeMillis() - start);
        }
        {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                this.userMapper.findById(1L);
            }
            System.out.println(System.currentTimeMillis() - start);
        }
        {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                this.userMapper.findByName("博狗");
            }
            System.out.println(System.currentTimeMillis() - start);
        }
        {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                this.userMapper.findById(1L);
            }
            System.out.println(System.currentTimeMillis() - start);
        }
    }
}