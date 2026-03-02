package com.maitrisetcf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Required to make JPA repositories work.
 */
@Configuration
@EnableJpaRepositories({"com.maitrisetcf.infrastructure.adapter.out.persistence.repository"})
@EnableTransactionManagement
public class DatabaseConfiguration {
}
