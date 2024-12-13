package com.hand.demo.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.app.service.InvoiceApplyLineService;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import io.choerodon.core.exception.CommonException;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InvoiceApplyLineImportServiceImpl extends BatchImportHandler {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;

    @Autowired
    InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Autowired
    InvoiceApplyLineService invoiceApplyLineService;

    @Override
    public Boolean doImport(List<String> data) {
        try {
            List<InvoiceApplyLine> invoiceApplyLines = new ArrayList<>();
            boolean hasError = false;

            for (int i = 0; i < data.size(); i++) {
                try {
                    InvoiceApplyLine invoiceApplyLine = objectMapper.readValue(data.get(i), InvoiceApplyLine.class);

                    if (invoiceApplyLine.getApplyLineId() != null) {
                        InvoiceApplyLine existingLine = invoiceApplyLineRepository.selectByPrimary(
                                invoiceApplyLine.getApplyLineId()
                        );
                        if (existingLine == null) {
                            throw new   IllegalStateException ("invalid");
                        }

                        invoiceApplyLine = existingLine;
                    } else {
                        invoiceApplyLine.setApplyLineId(null);
                    }

                    invoiceApplyLine.setApplyHeaderId(invoiceApplyLine.getApplyHeaderId());
                    invoiceApplyLine.setInvoiceName(invoiceApplyLine.getInvoiceName());
                    invoiceApplyLine.setContentName(invoiceApplyLine.getContentName());
                    invoiceApplyLine.setTaxClassificationNumber(invoiceApplyLine.getTaxClassificationNumber());
                    invoiceApplyLine.setUnitPrice(invoiceApplyLine.getUnitPrice());
                    invoiceApplyLine.setQuantity(invoiceApplyLine.getQuantity());
                    invoiceApplyLine.setTaxRate(invoiceApplyLine.getTaxRate());
                    invoiceApplyLine.setRemark(invoiceApplyLine.getRemark());
                    calculateList(invoiceApplyLine);

                    invoiceApplyLines.add(invoiceApplyLine);
                } catch (Exception e) {
                    addErrorMsg(i, String.format("Error at row %d: %s", i + 1, e.getMessage()));
                    hasError = true;
                }
            }

            if (!hasError && !invoiceApplyLines.isEmpty()) {
                invoiceApplyLineService.saveData(invoiceApplyLines);
            }

            return !hasError;
        } catch (Exception e) {
            String errorMsg = String.format("Batch import failed: %s", e.getMessage());
            throw new CommonException(errorMsg);
        }
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
}
