package xo.fredtan.lottolearn.common.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryResponseData<T> extends BasicResponseData {
    private final QueryResult<T> payload;

    public QueryResponseData(ResultCode resultCode, QueryResult<T> payload) {
        super(resultCode);
        this.payload = payload;
    }

    public QueryResponseData(QueryResult<T> payload) {
        this(CommonCode.OK, payload);
    }

    public static <T> QueryResponseData<T> ok(QueryResult<T> payload) {
        return new QueryResponseData<>(payload);
    }
}
