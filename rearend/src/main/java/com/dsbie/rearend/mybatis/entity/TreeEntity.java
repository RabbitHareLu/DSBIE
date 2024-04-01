package com.dsbie.rearend.mybatis.entity;

import com.dsbie.rearend.mybatis.SysDataSource;
import com.dsbie.rearend.mybatis.handler.NodeInfoHandler;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author WCG
 */
@Data
@Table(value = "tree", dataSource = SysDataSource.DATASOURCE_NAME)
@NoArgsConstructor
@AllArgsConstructor
public class TreeEntity {

    @Id("id")
    private Integer id;

    @Column("parent_node_id")
    private Integer parentNodeId;

    @Column("node_name")
    private String nodeName;

    @Column("node_type")
    private String nodeType;

    @Column("node_comment")
    private String nodeComment;

    @Column("node_path")
    private String nodePath;

    @Column(value = "node_info", typeHandler = NodeInfoHandler.class)
    private Map<String, String> nodeInfo;

    @Column(ignore = true)
    private List<TreeEntity> child;

}
