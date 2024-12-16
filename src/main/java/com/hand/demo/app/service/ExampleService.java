package com.hand.demo.app.service;

import com.hand.demo.api.dto.HfleUploadDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * ExampleService
 */
public interface ExampleService {


    Page<HfleUploadDTO> getInfo(PageRequest pageRequest, HfleUploadDTO hfleUploadDTO, Long organizationId);
}
