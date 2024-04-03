package com.dsbie.rearend.manager.datasource.type.jdbc;

import com.dsbie.rearend.config.ConfigParam;
import lombok.Data;

/**
 * @author WCG
 */
@Data
public class JdbcConfig {

    /**
     * key
     */
    private String key;

    /**
     * driver
     */
    private String driver;

    /**
     * url
     */
    @ConfigParam(name = "URL", key = "jdbcUrl", must = true)
    private String jdbcUrl;

    /**
     * 用户名
     */
    @ConfigParam(name = "用户名", key = "username", must = true)
    private String username;

    /**
     * 密码
     */
    @ConfigParam(name = "密码", key = "password", must = true)
    private String password;

}
