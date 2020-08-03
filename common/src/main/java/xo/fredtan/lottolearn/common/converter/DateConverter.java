package xo.fredtan.lottolearn.common.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class DateConverter implements Converter<String, Date> {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public Date convert(String s) {
        try {
            return dateFormat.parse(s);
        } catch (ParseException e) {
            log.error("Convert Date from String Error: {}", s);
        }
        return null;
    }
}
