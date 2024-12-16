package com.hand.demo.infra.mapper;

import com.hand.demo.api.dto.HfleUploadDTO;
import com.hand.demo.api.dto.InvoiceHeaderDTO;
import io.choerodon.mybatis.common.BaseMapper;
import com.hand.demo.domain.entity.Example;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 */
public interface ExampleMapper extends BaseMapper<Example> {
    List<HfleUploadDTO> selectList( HfleUploadDTO hfleUploadDTO);
}
