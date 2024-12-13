package com.hand.demo.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * (InvoiceApplyLine)应用服务
 *
 * @author
 * @since 2024-12-06 08:07:20
 */
public interface InvoiceApplyLineMapper extends BaseMapper<InvoiceApplyLine> {
    /**
     * 基础查询
     *
     * @param invoiceApplyLine 查询条件
     * @return 返回值
     */
    List<InvoiceApplyLine> selectList(InvoiceApplyLine invoiceApplyLine);
//    List<InvoiceApplyLine> selectLinesByHeaderId(@Param("headerId") Long headerId);
    List<InvoiceApplyLine> selectApplyHeader(@Param("applyHeaderIdlist") List<Long> applyHeaderIdlist);

}

