
package com.qcl.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * DataSource + MyBatis 配置
 */

@Configuration
@MapperScan(basePackages = "com.qcl.greptime.dao", sqlSessionFactoryRef = "greptimeSqlSessionFactory")
public class GreptimeDataSourceConfig {

    @Value("${greptime.datasource.url}")
    private String jdbcUrl;

    @Value("${greptime.datasource.username:}")
    private String username;

    @Value("${greptime.datasource.password:}")
    private String password;

    @Bean(name = "greptimeDataSource")
    public DataSource greptimeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        // 可以根据需要增加 driver-class-name 等配置
        return new HikariDataSource(config);
    }

    @Bean(name = "greptimeTransactionManager")
    public DataSourceTransactionManager greptimeTransactionManager() {
        return new DataSourceTransactionManager(greptimeDataSource());
    }

    @Bean(name = "greptimeSqlSessionFactory")
    public SqlSessionFactory greptimeSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(greptimeDataSource());
        // Greptime-specific mapper xml 的位置
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mapper/greptime/*.xml"));
        return sessionFactory.getObject();
    }
}
