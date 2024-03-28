package com.dsbie.rearend.common.utils;

import com.dsbie.rearend.manager.datasource.jdbc.JdbcConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @author WCG
 */
public class DataSourceUtil {

    public static DataSource createDataSource(JdbcConfig jdbcConfig) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(jdbcConfig.getDriver());
        hikariDataSource.setJdbcUrl(jdbcConfig.getJdbcUrl());
        hikariDataSource.setUsername(jdbcConfig.getUsername());
        hikariDataSource.setPassword(jdbcConfig.getPassword());
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setMaxLifetime(1800000);
        hikariDataSource.setKeepaliveTime(30000);
        hikariDataSource.setConnectionTestQuery("SELECT 1");
        hikariDataSource.setIdleTimeout(600000);
        return hikariDataSource;
    }

}
