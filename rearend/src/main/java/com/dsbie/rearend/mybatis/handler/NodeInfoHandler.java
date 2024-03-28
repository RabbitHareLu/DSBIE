package com.dsbie.rearend.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.dsbie.rearend.common.utils.JsonUtil;
import com.dsbie.rearend.common.utils.StringUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * node_info 处理器
 *
 * @author WCG
 */
public class NodeInfoHandler implements TypeHandler<Map<String, String>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Map<String, String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            String value = JsonUtil.writeObjectToJson(parameter);
            ps.setString(i, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getResult(ResultSet rs, String columnName) throws SQLException {
        return getInfoMap(rs.getString(columnName));
    }

    @Override
    public Map<String, String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getInfoMap(rs.getString(columnIndex));
    }

    @Override
    public Map<String, String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getInfoMap(cs.getString(columnIndex));
    }

    private static Map<String, String> getInfoMap(String json) {
        if (StringUtil.isBlank(json)) {
            return new HashMap<>();
        }
        try {
            return JsonUtil.readJsonToMap(json, String.class, String.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
