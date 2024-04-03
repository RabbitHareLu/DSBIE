package com.dsbie.rearend.manager.datasource.type.file;

import com.dsbie.rearend.config.ConfigParamUtil;
import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.job.element.BaseRow;
import com.dsbie.rearend.job.model.JobModel;
import com.dsbie.rearend.job.model.SinkConfig;
import com.dsbie.rearend.job.model.SourceConfig;
import com.dsbie.rearend.manager.datasource.KDataSourceHandler;
import com.dsbie.rearend.manager.datasource.type.file.parser.FileParser;
import com.dsbie.rearend.manager.datasource.type.file.parser.FileParserType;
import com.dsbie.rearend.manager.datasource.type.jdbc.model.TableMetadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 抽象文件类处理器
 *
 * @author WCG
 */
public abstract class AbstractFileHandler implements KDataSourceHandler {

    protected final Properties properties;

    protected final FileConfig fileConfig;

    public AbstractFileHandler(Properties properties) throws KToolException {
        this.properties = properties;
        this.fileConfig = ConfigParamUtil.buildConfig(properties, FileConfig.class);
        this.fileConfig.setKey(UUID.randomUUID().toString());
    }

    @Override
    public void close() {

    }

    @Override
    public void testConn() {

    }

    @Override
    public void conn() {

    }

    @Override
    public void disConn() {

    }

    @Override
    public List<String> selectAllSchema() {
        throw new RuntimeException("文件类数据源暂不支持此功能！");
    }

    @Override
    public List<String> selectAllTable(String schema) {
        throw new RuntimeException("文件类数据源暂不支持此功能！");
    }

    @Override
    public TableMetadata selectTableMetadata(String schema, String tableName) {
        throw new RuntimeException("文件类数据源暂不支持此功能！");
    }

    @Override
    public void selectData(JobModel jobModel, Consumer<Stream<BaseRow>> consumer) {
        SourceConfig sourceConfig = jobModel.getSourceConfig();
        FileParser parser = FileParserType.valueOf(sourceConfig.getFileType()).getParser();
        try (InputStream inputStream = getInputStream(sourceConfig.getConfigValue())) {
            parser.read(inputStream, sourceConfig.getSeparator(), consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void syncData(JobModel jobModel, Stream<BaseRow> baseRow) {
        SinkConfig sinkConfig = jobModel.getSinkConfig();
        FileParser parser = FileParserType.valueOf(sinkConfig.getFileType()).getParser();
        String filePath = sinkConfig.getDirPath() + File.separator + System.currentTimeMillis() + "." + sinkConfig.getFileType();
        try (OutputStream outputStream = getOutputStream(filePath)) {
            parser.write(outputStream, sinkConfig.getSeparator(), baseRow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract InputStream getInputStream(String filePath) throws IOException;

    protected abstract OutputStream getOutputStream(String filePath) throws IOException;

}
