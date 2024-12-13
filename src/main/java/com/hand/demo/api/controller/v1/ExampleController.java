package com.hand.demo.api.controller.v1;

import com.hand.demo.api.dto.PrefixDTO;
import com.hand.demo.app.service.ExampleService;
import com.hand.demo.domain.repository.ExampleRepository;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import com.hand.demo.config.SwaggerTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * API接口
 */
@Api(tags = SwaggerTags.EXAMPLE)
@RestController("exampleController.v1")
@RequestMapping("/v1/{organizationId}/getConfigFile")
public class ExampleController extends BaseController {

    @Autowired
    private ExampleService exampleService;
    @Autowired
    private ExampleRepository exampleRepository;

    @ApiOperation(value = "根据ID获取")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<List<PrefixDTO>> getData(@PathVariable Long organizationId) {
        return Results.success(exampleService.getFileConfig(organizationId));
    }


}


