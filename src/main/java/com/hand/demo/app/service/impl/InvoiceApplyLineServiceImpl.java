package com.hand.demo.app.service.impl;

import com.hand.demo.api.dto.InvoiceLineDTO;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceApplyLineService;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * (InvoiceApplyLine)应用服务
 *
 * @author
 * @since 2024-12-06 08:07:21
 */
@Service
public class InvoiceApplyLineServiceImpl implements InvoiceApplyLineService {
    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;
    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;


    @Override
    public Page<InvoiceApplyLine> selectList(PageRequest pageRequest, InvoiceApplyLine invoiceApplyLine) {
        return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyLineRepository.selectList(invoiceApplyLine));
    }

    @Override
    public void saveData(List<InvoiceApplyLine> invoiceApplyLines) {
        List<InvoiceApplyLine> insertList = invoiceApplyLines.stream().filter(line -> line.getApplyLineId() == null).collect(Collectors.toList());
        List<InvoiceApplyLine> updateList = invoiceApplyLines.stream().filter(line -> line.getApplyLineId() != null).collect(Collectors.toList());

        invoiceApplyLineRepository.batchInsertSelective(insertList);
        invoiceApplyLineRepository.batchUpdateByPrimaryKeySelective(updateList);

        insertList.forEach(this::calculateList);
        //Todo change name headerIds and change the name of invoiceApplyLineRepository.selectApplyHeader tobe invoiceApplyLineRepository.selectApplyList
        Set<Long> setHeaderid = invoiceApplyLines.stream().map(InvoiceApplyLine::getApplyHeaderId).collect(Collectors.toSet());
        List<InvoiceApplyLine> takeLine = invoiceApplyLineRepository.selectApplyHeader(new ArrayList<>(setHeaderid));
//        List<InvoiceApplyLine> takeLine = invoiceApplyLineRepository.selectByCondition(Condition.builder(InvoiceApplyLine.class)
//                .where(Sqls.custom().andIn(InvoiceApplyLine.FIELD_APPLY_HEADER_ID,setHeaderid))
//                .build()).selectApplyHeader(new ArrayList<>(setHeaderid));

        updateHeader(takeLine);

    }
    public void calculateList(InvoiceApplyLine invoiceApplyLine){
        BigDecimal unitPrice = invoiceApplyLine.getUnitPrice();
        BigDecimal quantity = invoiceApplyLine.getQuantity();
        BigDecimal taxRate = invoiceApplyLine.getTaxRate();


        BigDecimal totalAmount = unitPrice.multiply(quantity);
        invoiceApplyLine.setTotalAmount(totalAmount);

        BigDecimal taxAmount = totalAmount.multiply(taxRate);
        invoiceApplyLine.setTaxAmount(taxAmount);

        BigDecimal excludeTaxAmount = totalAmount.subtract(taxAmount);
        invoiceApplyLine.setExcludeTaxAmount(excludeTaxAmount);

    }
    public void updateHeader(List<InvoiceApplyLine> invoiceApplyLine){
        //ambil id line
        Map<Long, List<InvoiceApplyLine>> lineInHeaders = invoiceApplyLine.stream().collect(Collectors.groupingBy(InvoiceApplyLine::getApplyHeaderId));
        List<InvoiceApplyHeader> headers = invoiceApplyHeaderRepository.selectByIds(
                lineInHeaders.keySet().stream().map(Object::toString).collect(Collectors.joining(","))
        );
        // cal
        headers.forEach(header -> calculateHeader(header, lineInHeaders.get(header.getApplyHeaderId())));

        // update data header
        invoiceApplyHeaderRepository.batchUpdateByPrimaryKeySelective(headers);
    }
    public void calculateHeader(InvoiceApplyHeader invoiceApplyHeader,List<InvoiceApplyLine> invoiceApplyLines){
        BigDecimal totalAmount = invoiceApplyLines.stream().map(InvoiceApplyLine::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        invoiceApplyHeader.setTotalAmount(totalAmount);

        BigDecimal taxAmount = invoiceApplyLines.stream().map(InvoiceApplyLine::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        invoiceApplyHeader.setTaxAmount(taxAmount);

        BigDecimal excludeTaxAmount = invoiceApplyLines.stream().map(InvoiceApplyLine::getExcludeTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        invoiceApplyHeader.setExcludeTaxAmount(excludeTaxAmount);

    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<InvoiceLineDTO> exportLine(InvoiceApplyLine invoiceApplyLine) {
        try {
            List<InvoiceApplyLine> lines = invoiceApplyLineRepository.selectList(invoiceApplyLine);

            Set<Long> headerIds = lines.stream()
                    .map(InvoiceApplyLine::getApplyHeaderId)
                    .collect(Collectors.toSet());

            List<InvoiceApplyHeader> headers = invoiceApplyHeaderRepository.selectByIds(
                    String.join(",", headerIds.stream().map(Object::toString).collect(Collectors.toList()))
            );

            Map<Long, String> headerNumberMap = headers.stream()
                    .collect(Collectors.toMap(InvoiceApplyHeader::getApplyHeaderId, InvoiceApplyHeader::getApplyHeaderNumber));

            List<InvoiceLineDTO> result = lines.stream().map(line -> {
                InvoiceLineDTO dto = new InvoiceLineDTO();

                BeanUtils.copyProperties(line, dto, "attribute1", "attribute2", "attribute3", "attribute4", "attribute5",
                        "attribute6", "attribute7", "attribute8", "attribute9", "attribute10",
                        "attribute11", "attribute12", "attribute13", "attribute14", "attribute15");

                dto.setApplyHeaderNumber(headerNumberMap.get(line.getApplyHeaderId()));

                return dto;
            }).collect(Collectors.toList());

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

