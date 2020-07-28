package xo.fredtan.lottolearn.common.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UniqueQueryResponseData<T> extends BasicResponseData {
    private T payload;

    public UniqueQueryResponseData(ResultCode resultCode, T payload) {
        super(resultCode);
        this.payload = payload;
    }

    public UniqueQueryResponseData(T payload) {
        this(CommonCode.OK, payload);
    }

    public static <T> UniqueQueryResponseData<T> ok(T payload) {
        return new UniqueQueryResponseData<>(payload);
    }
}
