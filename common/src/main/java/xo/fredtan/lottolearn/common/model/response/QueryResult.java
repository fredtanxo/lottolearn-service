package xo.fredtan.lottolearn.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QueryResult<T> {
    private Long total;
    private List<T> data;
}
