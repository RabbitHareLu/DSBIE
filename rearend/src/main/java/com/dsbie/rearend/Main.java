package com.dsbie.rearend;

import com.dsbie.rearend.api.DataSourceApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        KToolsContext instance = KToolsContext.getInstance();

        DataSourceApi dataSourceApi = instance.getApi(DataSourceApi.class);

        Map<String, String> map = new HashMap<>();
        map.put("jdbcUrl", "jdbc:mysql://192.168.0.33:3306/kdm_sys");
        map.put("username", "kdm_sys");
        map.put("password", "Pqv]!/T)-1p");

        dataSourceApi.conn("111", "MYSQL", map);
        List<String> list = dataSourceApi.selectAllSchema("111");
//        List<String> list = dataSourceApi.selectAllTable("111", "performance_schema");
//        List<String> list = dataSourceApi.selectAllTable("111", "kdm_sys");
        list.forEach(System.out::println);

    }
}