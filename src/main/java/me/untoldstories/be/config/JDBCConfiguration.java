package me.untoldstories.be.config;

import me.untoldstories.be.config.pojos.DatabaseConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class JDBCConfiguration {
    @Bean
    public DataSource mysqlDataSource() {
        DatabaseConfiguration dbConfig = ConfigurationManager.getDatabaseConfiguration();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dbConfig.url);
        dataSource.setUsername(dbConfig.userName);
        dataSource.setPassword(dbConfig.password);

        return dataSource;
    }
}
