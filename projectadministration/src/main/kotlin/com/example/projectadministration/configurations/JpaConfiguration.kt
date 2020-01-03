package com.example.projectadministration.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

@Configuration
class JpaConfiguration {

    @Bean
    @Primary
    fun transactionManager(em: EntityManagerFactory): JpaTransactionManager {
        return JpaTransactionManager(em)
    }

}