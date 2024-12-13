package com.hand.demo.api.controller.v1;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * (InvoiceApplyHeader)表控制层
 *
 * @author
 * @since 2024-12-06 08:07:07
 */

@RestController("invoiceApplyHeaderController.v1")
@RequestMapping("/v1/{organizationId}/invoice-apply-headers")
public class InvoiceApplyHeaderController extends BaseController {

    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Autowired
    private InvoiceApplyHeaderService invoiceApplyHeaderService;
    @Autowired
    private RedisHelper redisHelper;

    @ApiOperation(value = "列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<InvoiceHeaderDTO>> list(InvoiceHeaderDTO invoiceApplyHeader, @PathVariable Long organizationId,
                                                       @ApiIgnore @SortDefault(value = InvoiceApplyHeader.FIELD_APPLY_HEADER_ID,
                                                                 direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<InvoiceHeaderDTO> list = invoiceApplyHeaderService.selectList(pageRequest, invoiceApplyHeader);
        return Results.success(list);
    }

    @ApiOperation(value = "明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{applyHeaderId}")
    public ResponseEntity<InvoiceApplyHeader> detail(@PathVariable Long applyHeaderId) {
        InvoiceApplyHeader invoiceApplyHeader = invoiceApplyHeaderRepository.selectByPrimary(applyHeaderId);
        return Results.success(invoiceApplyHeader);
    }
    @ApiOperation(value = "details")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/details/{applyHeaderId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public InvoiceHeaderDTO detailAndLine(@PathVariable Long applyHeaderId) {
        return invoiceApplyHeaderService.detailAndLine(applyHeaderId);
    }

    @ApiOperation(value = "创建或更新")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<InvoiceApplyHeader>> save(@PathVariable Long organizationId, @RequestBody List<InvoiceApplyHeader> invoiceApplyHeaders) {
        validObject(invoiceApplyHeaders);
        SecurityTokenHelper.validTokenIgnoreInsert(invoiceApplyHeaders);
        invoiceApplyHeaders.forEach(item -> item.setTenantId(organizationId));
        invoiceApplyHeaderService.saveData(invoiceApplyHeaders);
        return Results.success(invoiceApplyHeaders);
    }

    @ApiOperation(value = "删除")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody List<InvoiceApplyHeader> invoiceApplyHeaders) {
        SecurityTokenHelper.validToken(invoiceApplyHeaders);
        //Todo delete the logic in the controller

        for (InvoiceApplyHeader header : invoiceApplyHeaders) {
            //Todo jangan pakai "*"
            String headerCacheKey = "48209:IAH" + ":*";
            redisHelper.delKey(headerCacheKey);
        }
        invoiceApplyHeaderRepository.softDelete(invoiceApplyHeaders);
        return Results.success();
    }
//    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
//    @DeleteMapping("/{applyHeaderId}")
//    public ResponseEntity<InvoiceApplyHeader> deleteById(@PathVariable Long applyHeaderId) {
//        return invoiceApplyHeaderService.deleteById(applyHeaderId);
//    }
    @ApiOperation(value = "Export Invoice Apply Header Data")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(InvoiceHeaderDTO.class)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<InvoiceHeaderDTO>> export(InvoiceHeaderDTO invoiceApplyHeaderDTO,
                                                              ExportParam exportParam,
                                                              HttpServletResponse response,
                                                              @PathVariable String organizationId) {
        return Results.success(invoiceApplyHeaderService.exportHeader(invoiceApplyHeaderDTO));
    }
}
