package com.dsbie.rearend.manager.datasource.type.file.parser;

import com.dsbie.rearend.job.element.BaseRow;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 文件读取器
 * 文件写入器
 *
 * @author WCG
 */
public interface FileParser {

    /**
     * 读取文件
     */
    void read(InputStream inputStream, String separator, Consumer<Stream<BaseRow>> consumer);

    /**
     * 写入文件
     */
    void write(OutputStream outputStream, String separator, Stream<BaseRow> stream);

}
