package xo.fredtan.lottolearn.api.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("JWK")
public interface JwkControllerApi {
    @ApiOperation("获取现有的JWKSet")
    String fetchJWKSet();
}
