package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;
import xo.fredtan.lottolearn.domain.course.Term;

@Data
public class ModifyTermRequest extends Term implements RequestData {
}
