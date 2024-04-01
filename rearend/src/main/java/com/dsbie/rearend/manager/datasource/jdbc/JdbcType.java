package com.dsbie.rearend.manager.datasource.jdbc;

import com.dsbie.rearend.config.ConfigParamUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.DataSourceType;
import com.dsbie.rearend.manager.datasource.KDataSourceHandler;
import com.dsbie.rearend.manager.datasource.Type;
import com.dsbie.rearend.manager.datasource.jdbc.adaper.mysql.MysqlConfig;
import com.dsbie.rearend.manager.datasource.jdbc.adaper.mysql.MysqlHandler;
import com.dsbie.rearend.manager.datasource.jdbc.adaper.oracle.OracleConfig;
import com.dsbie.rearend.manager.datasource.jdbc.adaper.oracle.OracleHandler;
import com.dsbie.rearend.manager.datasource.model.KDataSourceConfig;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

/**
 * jdbc类型
 *
 * @author WCG
 */
public enum JdbcType implements Type {

    MYSQL(MysqlConfig.class, MysqlHandler.class),
    ORACLE(OracleConfig.class, OracleHandler.class);

    private final Class<? extends JdbcConfig> configClass;

    private final Class<? extends AbstractJdbcHandler> handerClass;

    JdbcType(Class<? extends JdbcConfig> configClass, Class<? extends AbstractJdbcHandler> handerClass) {
        this.configClass = configClass;
        this.handerClass = handerClass;
    }

    /**
     * 数据源元数据
     */
    @Override
    public KDataSourceMetadata getMetadata() {
        List<KDataSourceConfig> configs = ConfigParamUtil.parseConfigClass(this.configClass);
        return KDataSourceMetadata.of(DataSourceType.JDBC.name(), this.name(), configs);
    }

    /**
     * 创建数据源处理器
     *
     * @param properties 配置消息
     * @return 数据源处理器
     */
    @Override
    public KDataSourceHandler createDataSourceHandler(Properties properties) throws KToolException {
        try {
            return this.handerClass.getConstructor(Properties.class).newInstance(properties);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
