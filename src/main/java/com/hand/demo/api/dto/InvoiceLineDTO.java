package com.hand.demo.api.dto;

import com.hand.demo.domain.entity.InvoiceApplyLine;
import lombok.Getter;
import lombok.Setter;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

@Setter
@Getter
@ExcelSheet(zh = "Invoice Apply Info", en = "INVHEADER_BATCH")
public class InvoiceLineDTO extends InvoiceApplyLine {
    @ExcelColumn(zh = "Apply Header Number", en = "Apply Header Number")
    private String applyHeaderNumber;
}
