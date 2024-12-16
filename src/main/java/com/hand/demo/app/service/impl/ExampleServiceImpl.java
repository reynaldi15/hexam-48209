package com.hand.demo.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.HfleUploadDTO;
import com.hand.demo.app.service.ExampleService;
import com.hand.demo.infra.mapper.ExampleMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.apaas.common.userinfo.domain.UserVO;
import org.hzero.boot.apaas.common.userinfo.infra.feign.IamRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * ExampleServiceImpl
 */
@Service
public class ExampleServiceImpl implements ExampleService {
    @Autowired
    private ExampleMapper exampleMapper;
    @Autowired
    private IamRemoteService iamRemoteService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Page<HfleUploadDTO> getInfo(PageRequest pageRequest, HfleUploadDTO hfleUploadDTO, Long organizationId){
        try{
            ResponseEntity<String> data = iamRemoteService.selectSelf();
            String body = data.getBody();
            UserVO userVO = objectMapper.readValue(body, UserVO.class);
            Boolean tenantAdminFlag = userVO.getTenantAdminFlag();
            HfleUploadDTO hfleUploadDTO1 = new HfleUploadDTO();
            if(tenantAdminFlag != null && !tenantAdminFlag)
            {
                hfleUploadDTO1.setCreatedBy(userVO.getId().toString());
            }
            return PageHelper.doPageAndSort(pageRequest, () -> exampleMapper.selectList(hfleUploadDTO1));
        }catch (Exception e){
            throw new RuntimeException("Failed to get user info");
        }
    }



}
