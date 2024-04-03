package com.dsbie.rearend.manager.datasource.type.file;

import com.dsbie.rearend.config.ConfigParamUtil;
import com.dsbie.rearend.manager.datasource.DataSourceType;
import com.dsbie.rearend.manager.datasource.KDataSourceHandler;
import com.dsbie.rearend.manager.datasource.Type;
import com.dsbie.rearend.manager.datasource.model.KDataSourceConfig;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;
import com.dsbie.rearend.manager.datasource.type.file.adaper.local.LocalFileHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

/**
 * 文件类型
 *
 * @author WCG
 */
public enum FileType implements Type {

    LOCAL_FILE(FileConfig.class, LocalFileHandler.class);

    private final Class<? extends FileConfig> configClass;

    private final Class<? extends AbstractFileHandler> handerClass;

    FileType(Class<? extends FileConfig> configClass, Class<? extends AbstractFileHandler> handerClass) {
        this.configClass = configClass;
        this.handerClass = handerClass;
    }

    @Override
    public KDataSourceMetadata getMetadata() {
        List<KDataSourceConfig> configs = ConfigParamUtil.parseConfigClass(this.configClass);
        return KDataSourceMetadata.of(DataSourceType.FILE.name(), this.name(), configs);
    }

    @Override
    public KDataSourceHandler createDataSourceHandler(Properties properties) {
        try {
            return this.handerClass.getConstructor(Properties.class).newInstance(properties);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
