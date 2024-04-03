package com.dsbie.rearend.manager.datasource.type.file.parser;

import com.dsbie.rearend.manager.datasource.type.file.parser.impl.CsvFileParser;

import java.lang.reflect.InvocationTargetException;

/**
 * 文件解析器类型
 *
 * @author WCG
 */
public enum FileParserType {

    CSV(CsvFileParser.class);

    private final Class<? extends FileParser> parserClass;

    private volatile FileParser parser;

    FileParserType(Class<? extends FileParser> parserClass) {
        this.parserClass = parserClass;
    }

    public FileParser getParser() {
        if (this.parser == null) {
            synchronized (this) {
                if (this.parser == null) {
                    try {
                        this.parser = this.parserClass.getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException("文件解析器实例化失败！", e);
                    }
                }
            }
        }
        return this.parser;
    }

}
