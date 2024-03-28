package com.dsbie.rearend.mybatis;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.SysDataSource;
import com.dsbie.rearend.mybatis.entity.PropEntity;
import com.dsbie.rearend.mybatis.mapper.PropMapper;
import com.dsbie.rearend.mybatis.mapper.TreeMapper;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 *
 * @author WCG
 */
@Slf4j
public class MybatisContext {

    private final MybatisFlexBootstrap bootstrap;

    public MybatisContext(DataSource dataSource) {
        FlexGlobalConfig.getDefaultConfig().setPrintBanner(false);
        this.bootstrap = MybatisFlexBootstrap.getInstance()
                .addDataSource(SysDataSource.DATASOURCE_NAME, dataSource)
                .addMapper(PropMapper.class)
                .addMapper(TreeMapper.class)
                .start();

        // 修改impala方言
        DialectFactory.registerDialect(DbType.IMPALA, new CommonsDialectImpl(KeywordWrap.BACK_QUOTE, LimitOffsetProcessor.POSTGRESQL));
    }

    public <T> T getMapper(Class<T> tClass) {
        return bootstrap.getMapper(tClass);
    }

    public Properties loadAllProperties() {
        List<PropEntity> propEntities = this.getMapper(PropMapper.class).selectAll();
        Properties properties = new Properties();
        propEntities.forEach(prop -> properties.put(prop.getKey(), prop.getValue()));
        return properties;
    }

    public void addDataSource(String key, DataSource dataSource) {
        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        flexDataSource.addDataSource(key, dataSource);
    }

    public void removeDataSource(String key) {
        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        HikariDataSource dataSource = (HikariDataSource) flexDataSource.getDataSourceMap().get(key);
        dataSource.close();
        flexDataSource.removeDatasource(key);
    }

    public boolean existDataSource(String key) {
        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        return flexDataSource.getDataSourceMap().containsKey(key);
    }

    public DataSource getDataSource(String key) throws KToolException {
        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        if (!flexDataSource.getDataSourceMap().containsKey(key)) {
            throw new KToolException("数据源不存在！");
        }
        return flexDataSource.getDataSourceMap().get(key);
    }

    public void showdown() {
        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        ArrayList<String> keys = new ArrayList<>(flexDataSource.getDataSourceMap().keySet());
        keys.forEach(key -> {
            HikariDataSource dataSource = (HikariDataSource) flexDataSource.getDataSourceMap().get(key);
            dataSource.close();
            flexDataSource.removeDatasource(key);
        });
    }
}
