package com.qcl.config.convert;

import com.qcl.entity.Logs;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;





@Component
@ReadingConverter
public class MapToAgentLogsConverter implements Converter<Map<String, Object>, Logs> {

    @Override
    public Logs convert(Map<String, Object> source) {
        Logs logs = new Logs();

        if (source.containsKey("content")) {
            logs.setContent((String) source.get("content"));
        }

        if (source.containsKey("level")) {
            logs.setLevel((String) source.get("level"));
        }

        if (source.containsKey("timestamp")) {
            logs.setTimestamp((String) source.get("timestamp"));
        }

        return logs;
    }
}