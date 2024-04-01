package com.dsbie.rearend.mybatis.entity;

import com.dsbie.rearend.mybatis.SysDataSource;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @author WCG
 */
@Data
@Table(value = "PROP", dataSource = SysDataSource.DATASOURCE_NAME)
public class PropEntity {

    @Id("ID")
    private Integer id;

    @Column("KEY")
    private String key;

    @Column("VALUE")
    private String value;

}
