package com.xxbb.demo.configuration;

import com.xxbb.demo.domain.Account;
import com.xxbb.framework.simplespring.core.annotation.Bean;
import com.xxbb.framework.simplespring.core.annotation.Configuration;

/**
 * @author xxbb
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public Account getAccount(){
        return new Account();
    }
}
