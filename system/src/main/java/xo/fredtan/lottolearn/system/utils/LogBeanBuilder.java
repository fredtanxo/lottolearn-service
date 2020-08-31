package xo.fredtan.lottolearn.system.utils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class LogBeanBuilder {
    private static final DateFormat dateFormat;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static <T> T mapToBean(Map<String, String> map, Class<T> tClass) {
        try {
            T target = tClass.getConstructor().newInstance();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                try {
                    Field field = tClass.getDeclaredField(key);
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    if (String.class.equals(type)) {
                        field.set(target, value);
                    } else if (Date.class.equals(type)) {
                        field.set(target, dateFormat.parse(value));
                    }
                } catch (Throwable t) {
                    // Ignored
                }
            }
            return target;
        } catch (Throwable t) {
            // 无默认构造器，返回空对象
            return null;
        }
    }
}
