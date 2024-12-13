package com.hand.demo.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import com.hand.demo.domain.entity.InvoiceApplyLine;

import java.util.ArrayList;
import java.util.List;

/**
 * (InvoiceApplyLine)资源库
 *
 * @author
 * @since 2024-12-06 08:07:21
 */
public interface InvoiceApplyLineRepository extends BaseRepository<InvoiceApplyLine> {
    /**
     * 查询
     *
     * @param invoiceApplyLine 查询条件
     * @return 返回值
     */
    List<InvoiceApplyLine> selectList(InvoiceApplyLine invoiceApplyLine);

    /**
     * 根据主键查询（可关联表）
     *
     * @param applyLineId 主键
     * @return 返回值
     */
    InvoiceApplyLine selectByPrimary(Long applyLineId);

    List<InvoiceApplyLine> selectByHeaderId(Long applyHeaderId);


    List<InvoiceApplyLine> selectApplyHeader(List<Long> applyHeaderIdlist);
//    boolean validateData(String value, String lovCode);
}
