package com.dsbie.rearend.manager.datasource;

import com.dsbie.rearend.common.utils.DataSourceUtil;
import com.dsbie.rearend.manager.datasource.jdbc.JdbcConfig;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

/**
 * @author WCG
 */
public class SysDataSource {

    public static final String DATASOURCE_NAME = "system";
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:file:./db/KT";
    private static final String H2_USER_NAME = "system";
    private static final String H2_PASSWORD = "123456";


    public static DataSource init() {
        DataSource dataSource = DataSourceUtil.createDataSource(getDataSourceProperties());

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .table("flyway_schema_history")
                .outOfOrder(false)
                .validateOnMigrate(true)
                .load();

        flyway.migrate();

        return dataSource;
    }

    private static JdbcConfig getDataSourceProperties() {
        JdbcConfig dataSourceProperties = new JdbcConfig();
        dataSourceProperties.setKey(DATASOURCE_NAME);
        dataSourceProperties.setDriver(H2_DRIVER);
        dataSourceProperties.setJdbcUrl(H2_URL);
        dataSourceProperties.setUsername(H2_USER_NAME);
        dataSourceProperties.setPassword(H2_PASSWORD);
        return dataSourceProperties;
    }

}
