package com.hand.demo.infra.mapper;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import io.choerodon.mybatis.common.BaseMapper;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author
 * @since 2024-12-06 08:07:06
 */
public interface InvoiceApplyHeaderMapper extends BaseMapper<InvoiceApplyHeader> {
    /**
     * 基础查询
     *
     * @param invoiceApplyHeader 查询条件
     * @return 返回值
     */
//    This before
//    List<InvoiceHeaderDTO> selectList(@Param("header") InvoiceHeaderDTO invoiceApplyHeader);
//    This after
    List<InvoiceHeaderDTO> selectList(InvoiceHeaderDTO invoiceApplyHeader);
    InvoiceApplyHeader selectList(InvoiceApplyHeader invoiceApplyHeader);
    InvoiceApplyHeader selectHeaderNumber(@Param("applyHeaderNumber") String applyHeaderNumber);
}

