package com.dsbie.rearend.manager.datasource.type.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理器
 *
 * @author WCG
 */
public class TransactionManager {

    private final Connection connection;
    private final boolean supportTransaction;

    private TransactionManager(Connection connection, boolean supportTransaction) {
        this.connection = connection;
        this.supportTransaction = supportTransaction;
    }

    public static TransactionManager createTransactionManager(Connection connection, boolean supportTransaction) {
        return new TransactionManager(connection, supportTransaction);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (supportTransaction) {
            connection.setAutoCommit(autoCommit);
        }
    }

    public void commit() throws SQLException {
        if (supportTransaction) {
            connection.commit();
        }
    }

    public void rollback() throws SQLException {
        if (supportTransaction) {
            connection.rollback();
        }
    }

}
