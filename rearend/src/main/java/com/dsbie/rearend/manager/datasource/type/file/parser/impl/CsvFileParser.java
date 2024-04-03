package com.dsbie.rearend.manager.datasource.type.file.parser.impl;

import com.dsbie.rearend.exception.KToolException;
import com.dsbie.rearend.job.element.BaseColumn;
import com.dsbie.rearend.job.element.BaseRow;
import com.dsbie.rearend.job.element.DataType;
import com.dsbie.rearend.manager.datasource.type.file.parser.FileParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import de.siegmar.fastcsv.writer.CsvWriter;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author WCG
 */
public class CsvFileParser implements FileParser {

    @Override
    public void read(InputStream inputStream, String separator, Consumer<Stream<BaseRow>> consumer) {
        try (CsvReader<NamedCsvRecord> csvReader = CsvReader.builder()
                .fieldSeparator(separator.charAt(0))
                .ofNamedCsvRecord(new InputStreamReader(inputStream));
        ) {
            Stream<BaseRow> baseRowStream = csvReader.stream().map(namedCsvRow -> {
                List<String> header = namedCsvRow.getHeader();
                BaseRow baseRow = new BaseRow(header.size());
                header.forEach(key -> {
                    BaseColumn baseColumn = BaseColumn.create(key, namedCsvRow.getField(key), DataType.STRING);
                    baseRow.addField(baseColumn);
                });
                return baseRow;
            });
            consumer.accept(baseRowStream);
        } catch (IOException e) {
            throw new KToolException("文件读取异常！", e);
        }
    }

    @Override
    public void write(OutputStream outputStream, String separator, Stream<BaseRow> stream) {
        try (CsvWriter csvWriter = CsvWriter.builder()
                .fieldSeparator(separator.charAt(0))
                .build(new OutputStreamWriter(outputStream))
        ) {
            AtomicBoolean header = new AtomicBoolean(true);
            stream.forEach(baseRow -> {
                if (header.compareAndSet(true, false)) {
                    String[] headers = baseRow.getColumns().keySet().toArray(String[]::new);
                    // 写表头
                    csvWriter.writeRecord(headers);
                }
                // 写数据
                String[] data = baseRow.getColumns().values().stream()
                        .map(baseColumn -> String.valueOf(baseColumn.getData()))
                        .toArray(String[]::new);
                csvWriter.writeRecord(data);
            });
        } catch (IOException e) {
            throw new KToolException("文件写入异常！", e);
        }
    }

}
