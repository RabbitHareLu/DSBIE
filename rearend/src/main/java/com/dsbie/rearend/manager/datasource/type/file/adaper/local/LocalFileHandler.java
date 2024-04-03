package com.dsbie.rearend.manager.datasource.type.file.adaper.local;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.manager.datasource.type.file.AbstractFileHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * 本地文件处理器
 *
 * @author WCG
 */
public class LocalFileHandler extends AbstractFileHandler {

    public LocalFileHandler(Properties properties) throws KToolException {
        super(properties);
    }

    @Override
    protected InputStream getInputStream(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (Files.exists(path)) {
            return Files.newInputStream(path);
        }
        throw new IOException("文件未找到，文件路径：" + filePath);
    }

    @Override
    protected OutputStream getOutputStream(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (Files.exists(path.getParent())) {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            return Files.newOutputStream(path);
        }
        throw new IOException("文件夹不存在，文件路径：" + filePath);
    }

}
