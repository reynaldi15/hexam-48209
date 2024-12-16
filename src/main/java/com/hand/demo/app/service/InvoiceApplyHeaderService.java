package com.hand.demo.app.service;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.api.dto.ReportDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author
 * @since 2024-12-06 08:07:07
 */
public interface InvoiceApplyHeaderService {

    /**
     * 查询数据
     *
     * @param pageRequest         分页参数
     * @param invoiceApplyHeaders 查询条件
     * @return 返回值
     */
    Page<InvoiceHeaderDTO> selectList(PageRequest pageRequest, InvoiceHeaderDTO invoiceApplyHeaders);
    Page<InvoiceHeaderDTO> selectByTenant(PageRequest pageRequest, InvoiceHeaderDTO invoiceApplyHeaders);

    //Detail and line page

        InvoiceHeaderDTO detailWithLine(Long applyHeaderId);

    /**
     * 保存数据
     *
     * @param invoiceApplyHeaders 数据
     */
    void saveData(List<InvoiceApplyHeader> invoiceApplyHeaders);
    List<InvoiceHeaderDTO> exportHeader(InvoiceApplyHeader invoiceApplyHeader);


    ResponseEntity<InvoiceApplyHeader> deleteById(Long applyHeaderId);
    List<InvoiceApplyHeader>failed(InvoiceApplyHeader invoiceApplyHeader);

    // scheduler
    void scheduleTask(String delFlag, String appluStatus, String invoiceColor, String invoiceType);

    ResponseEntity<ReportDTO> reportExcel(Long organizationId, ReportDTO reportDTO);
}

