package com.dsbie.rearend.common.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * java stream 工具类
 *
 * @author WCG
 */
public class StreamUtil {

    public static Stream<Map<String, Object>> buildStream(final ResultSet resultSet) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new ResultSetIterator(resultSet),
                        Spliterator.ORDERED | Spliterator.IMMUTABLE
                ),
                false
        );
    }

    private static class ResultSetIterator implements Iterator<Map<String, Object>> {

        private final ResultSet resultSet;
        private final List<String> fieldList;

        private ResultSetIterator(final ResultSet resultSet) {
            this.resultSet = resultSet;
            try {
                fieldList = new ArrayList<>();
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    fieldList.add(resultSetMetaData.getColumnName(i));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean hasNext() {
            try {
                return resultSet.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Map<String, Object> next() {
            try {
                Map<String, Object> map = new LinkedHashMap<>();
                for (String fieldName : fieldList) {
                    map.put(fieldName, resultSet.getObject(fieldName));
                }
                return map;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}