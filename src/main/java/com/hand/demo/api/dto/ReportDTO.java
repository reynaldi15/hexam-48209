package com.hand.demo.api.dto;

import com.hand.demo.infra.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hzero.boot.platform.lov.annotation.LovValue;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReportDTO  {
    private String applyNumberFrom;
    private String applyNumberTo;
    private LocalDate submitTimeFrom;
    private LocalDate submitTimeTo;
    private LocalDate createdDateFrom;
    private LocalDate createdDateTo;
    @LovValue(lovCode = "HEXAM-INV-HEADER-TYPE-48209")
    private String InvoiceType;
    private List<String> applyStatus;
    private List<InvoiceHeaderDTO> headerDTO;
    private String tenantName;
}
