package com.dsbie.rearend;

import com.dsbie.rearend.api.DataSourceApi;
import com.dsbie.rearend.manager.datasource.model.KDataSourceMetadata;

import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        KToolsContext instance = KToolsContext.getInstance();


        Properties properties = instance.getProperties();
        properties.forEach((k, v) -> {
            System.out.println(k + "--->" + v);
        });

        DataSourceApi dataSourceApi = instance.getApi(DataSourceApi.class);
        List<KDataSourceMetadata> jdbc = dataSourceApi.getAllMetadata("JDBC");
        for (KDataSourceMetadata metadata : jdbc) {
            System.out.println(metadata);
        }
    }
}