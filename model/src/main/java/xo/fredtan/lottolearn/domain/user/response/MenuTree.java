package xo.fredtan.lottolearn.domain.user.response;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.response.ResponseData;
import xo.fredtan.lottolearn.domain.user.Menu;

import java.util.List;

@Data
public class MenuTree extends Menu implements ResponseData {
    private List<Menu> children;
}
