package com.dsbie.rearend;

import com.dsbie.rearend.api.DataSourceApi;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        KToolsContext instance = KToolsContext.getInstance();

        DataSourceApi dataSourceApi = instance.getApi(DataSourceApi.class);
        Map<String, KDataSourceMetadata> jdbc = dataSourceApi.getAllMetadata("JDBC");
        for (KDataSourceMetadata metadata : jdbc.values()) {
            System.out.println(metadata);
        }
    }
}