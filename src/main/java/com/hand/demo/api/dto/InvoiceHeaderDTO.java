package com.hand.demo.api.dto;

import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hzero.export.annotation.ExcelSheet;

import javax.persistence.Transient;
import java.util.List;

@Getter
@Setter
@ExcelSheet(zh = "Invoice Apply Info", en = "INVHEADER_BATCH")
public class InvoiceHeaderDTO extends InvoiceApplyHeader {

    @Transient
    @ApiModelProperty("Invoice Type Meaning")
    private String invoiceTypeMeaning;
    @Transient
    @ApiModelProperty("Invoice Color Meaning")
    private String invoiceColorMeaning;
    @Transient
    @ApiModelProperty("Apply Status Meaning")
    private String applyStatusMeaning;

    private List<InvoiceApplyLine> invoiceApplyLines;

}
