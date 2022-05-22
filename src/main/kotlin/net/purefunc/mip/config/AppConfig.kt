package net.purefunc.mip.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class AppConfig(
    private val connectionFactory: ConnectionFactory,
) {

    @Bean
    @ConditionalOnProperty(name = ["generate.data"], havingValue = "true")
    fun initializer() = CompositeDatabasePopulator()
        .apply {
            addPopulators(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
        }.let {
            val connectionFactoryInitializer = ConnectionFactoryInitializer()
            connectionFactoryInitializer.setConnectionFactory(connectionFactory)
            connectionFactoryInitializer.setDatabasePopulator(it)

            connectionFactoryInitializer
        }
}