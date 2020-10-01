package xo.fredtan.lottolearn.common.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerializeUtils {
    private static final Map<Class<?>, Schema<?>> cachedSchemas = new ConcurrentHashMap<>();
    private static final ThreadLocal<LinkedBuffer> linkedBufferLocal = ThreadLocal.withInitial(LinkedBuffer::allocate);

    public static <T> byte[] serialize(T obj) {
        Class<?> clazz = obj.getClass();
        LinkedBuffer buffer = linkedBufferLocal.get();
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) cachedSchemas.computeIfAbsent(clazz, key -> RuntimeSchema.getSchema(clazz));
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) cachedSchemas.computeIfAbsent(clazz, key -> RuntimeSchema.getSchema(clazz));
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
}
