package com.hand.demo.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;

import io.choerodon.core.exception.CommonException;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@ImportService(templateCode = "HEXAM-INVHEADER-47840-BATCH",
        sheetName = "INVHEADER_BATCH")
public class InvoiceApplyHeaderImportServiceImpl extends BatchImportHandler {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;
    @Autowired
    private InvoiceApplyHeaderServiceImpl invoiceApplyHeaderServiceImpl;

    @Autowired
    private LovAdapter lovAdapter;

    @Autowired
    private CodeRuleBuilder codeRuleBuilder;

    @Autowired
    private InvoiceApplyHeaderService invoiceApplyHeaderService;

    @Override
    public Boolean doImport(List<String> data) {
        try {
            List<InvoiceApplyHeader> invoiceApplyHeaderList = new ArrayList<>();
            boolean hasError = false;

            for (int i = 0; i < data.size(); i++) {
                try {
                    InvoiceHeaderDTO invoiceHeaderDTO = objectMapper.readValue(data.get(i),
                            InvoiceHeaderDTO.class);
                    InvoiceApplyHeader invoiceApplyHeader = new InvoiceApplyHeader();

                    if (invoiceHeaderDTO.getApplyHeaderNumber() != null) {
                        InvoiceApplyHeader existingHeader = invoiceApplyHeaderRepository.selectApplyHeaderNumber(
                                invoiceHeaderDTO.getApplyHeaderNumber());
                        if (existingHeader == null) {
                            throw new CommonException("error.invoice_header.not_found");
                        }
                        invoiceApplyHeader = existingHeader;
                    } else {
                        generateNumber(invoiceApplyHeader);
                        invoiceApplyHeader.setDelFlag(0);
                    }

                    invoiceApplyHeader.setApplyStatus(invoiceHeaderDTO.getApplyStatus());
                    invoiceApplyHeader.setSubmitTime(invoiceHeaderDTO.getSubmitTime());
                    invoiceApplyHeader.setInvoiceColor(invoiceHeaderDTO.getInvoiceColor());
                    invoiceApplyHeader.setInvoiceType(invoiceHeaderDTO.getInvoiceType());
                    invoiceApplyHeader.setBillToPerson(invoiceHeaderDTO.getBillToPerson());
                    invoiceApplyHeader.setBillToPhone(invoiceHeaderDTO.getBillToPhone());
                    invoiceApplyHeader.setBillToAddress(invoiceHeaderDTO.getBillToAddress());
                    invoiceApplyHeader.setBillToEmail(invoiceHeaderDTO.getBillToEmail());
                    invoiceApplyHeader.setRemark(invoiceHeaderDTO.getRemark());

                    validateData(invoiceApplyHeader, lovAdapter);

                    invoiceApplyHeaderList.add(invoiceApplyHeader);
                } catch (Exception e) {
                    addErrorMsg(i, String.format("Error at row %d: %s", i + 1, e.getMessage()));
                    hasError = true;
                }
            }

            if (!hasError && !invoiceApplyHeaderList.isEmpty()) {
                invoiceApplyHeaderService.saveData(invoiceApplyHeaderList);
            }

            return !hasError;
        } catch (Exception e) {
            String errorMsg = String.format("Batch import failed: %s", e.getMessage());
            throw new CommonException(errorMsg);
        }
    }
    public static void generateInvNumber(InvoiceApplyHeader invoiceApplyHeader, String codeRule,
                                         Map<String, String> variable, CodeRuleBuilder codeRuleBuilder){
        String invkNumber = codeRuleBuilder.generateCode(codeRule, variable);
        invoiceApplyHeader.setApplyHeaderNumber(invkNumber);
    }

    @Override
    public int getSize() {
        return BaseConstants.PAGE_SIZE;
    }
    private void generateNumber(InvoiceApplyHeader invoiceApplyHeader) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());
        String ruleCode = "HEXAM-INV-48209";
        Map<String, String> variableMap = new HashMap<>();
        variableMap.put("DATE_PART", datePart);
        String applyHeaderNumber = codeRuleBuilder.generateCode(ruleCode, variableMap);
        invoiceApplyHeader.setApplyHeaderNumber(applyHeaderNumber);
    }
    public static void validateData(InvoiceApplyHeader invoiceApplyHeader, LovAdapter lovAdapter){
        if(invoiceApplyHeader.getInvoiceType() != null){
            validateColoumn("HEXAM-INV-HEADER-TYPE-48209",invoiceApplyHeader.getInvoiceType(),
                    invoiceApplyHeader.getTenantId(),lovAdapter);
        }
        if(invoiceApplyHeader.getApplyStatus() != null){
            validateColoumn("HEXAM-INV-HEADER-STATUS-48209",invoiceApplyHeader.getApplyStatus(),
                    invoiceApplyHeader.getTenantId(),lovAdapter);
        }
        if(invoiceApplyHeader.getInvoiceColor() != null){
            validateColoumn("HEXAM-INV-HEADER-COLOR-48209",invoiceApplyHeader.getInvoiceColor(),
                    invoiceApplyHeader.getTenantId(),lovAdapter);
        }

    }
    public static void validateColoumn(String lovCode, String fieldValue, Long tenantId, LovAdapter lovAdapter) {
        List<LovValueDTO> lovValues = lovAdapter.queryLovValue(lovCode, tenantId);
        boolean isValid = lovValues.stream()
                .anyMatch(lovValue -> lovValue.getValue().equals(fieldValue));

        if (!isValid) {
            throw new CommonException("invalid", fieldValue);
        }
    }

}
