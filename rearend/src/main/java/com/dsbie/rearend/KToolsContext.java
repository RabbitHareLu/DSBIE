package com.dsbie.rearend;

import com.dsbie.rearend.api.DataSourceApi;
import com.dsbie.rearend.api.SystemApi;
import com.dsbie.rearend.manager.datasource.KDataSourceManager;
import com.dsbie.rearend.manager.datasource.SysDataSource;
import com.dsbie.rearend.manager.task.TaskManager;
import com.dsbie.rearend.manager.uid.IdGenerator;
import com.dsbie.rearend.mybatis.MybatisContext;
import com.dsbie.rearend.service.DataSourceService;
import com.dsbie.rearend.service.SystemService;
import lombok.Getter;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * K-Tools 上下文
 *
 * @author WCG
 */
@Getter
public class KToolsContext {

    private static volatile KToolsContext INSTANCE;

    private final MybatisContext mybatisContext;

    private final Properties properties;

    private final TaskManager taskManager;

    private final IdGenerator idGenerator;

    private final KDataSourceManager dataSourceManager;

    private KToolsContext() {
        // 初始化系统数据源
        DataSource dataSource = SysDataSource.init();
        // 初始化mybatis
        this.mybatisContext = new MybatisContext(dataSource);
        // 像mybatis注册系统数据源
        mybatisContext.addDataSource(SysDataSource.DATASOURCE_NAME, dataSource);
        // 初始化配置信息
        this.properties = this.mybatisContext.loadAllProperties();
        // 初始化任务管理器
        this.taskManager = new TaskManager();
        // 初始化id生成器
        this.idGenerator = new IdGenerator(mybatisContext);
        // 初始化数据源管理器
        this.dataSourceManager = new KDataSourceManager();
    }

    public static KToolsContext getInstance() {
        if (INSTANCE == null) {
            synchronized (KToolsContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new KToolsContext();
                }
            }
        }
        return INSTANCE;
    }

    public void showdown() {
        this.mybatisContext.showdown();
        this.taskManager.shutdown();
    }

    public <T> T getApi(Class<T> tClass) {
        if (tClass == SystemApi.class) {
            return tClass.cast(new SystemService());
        } else if (tClass == DataSourceApi.class){
            return tClass.cast(new DataSourceService());
        }
        return null;
    }
}
