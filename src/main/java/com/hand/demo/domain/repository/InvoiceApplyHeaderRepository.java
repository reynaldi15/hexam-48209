package com.hand.demo.domain.repository;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import org.hzero.mybatis.base.BaseRepository;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * (InvoiceApplyHeader)资源库
 *
 * @author
 * @since 2024-12-06 08:07:06
 */
public interface InvoiceApplyHeaderRepository extends BaseRepository<InvoiceApplyHeader> {
    /**
     * 查询
     *
     * @param invoiceApplyHeader 查询条件
     * @return 返回值
     */


    /**
     * 根据主键查询（可关联表）
     *
     * @return 返回值
     */
    List<InvoiceHeaderDTO> selectList(InvoiceHeaderDTO invoiceApplyHeader);
    InvoiceHeaderDTO selectByPrimary(Long applyHeaderId);
    void softDelete(List<InvoiceApplyHeader> invoiceApplyHeaders);
    InvoiceApplyHeader selectApplyHeaderNumber(String applyHeaderNumber);

//    List<InvoiceApplyLine> selectLinesByHeaderId(Long applyHeaderId);
        List<InvoiceApplyHeader> selectList(InvoiceApplyHeader invoiceApplyHeader);
}
