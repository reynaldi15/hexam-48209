package com.hand.demo.app.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.infra.constant.Constants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.scheduler.infra.annotation.JobHandler;
import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
import org.hzero.core.redis.RedisQueueHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JobHandler("48209:IAH")
@Slf4j
public class InvoiceHeaderJob implements IJobHandler {
    @Autowired
    private RedisQueueHelper redisQueueHelper;
    @Autowired
    private InvoiceApplyHeaderService invoiceApplyHeaderService;
    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ReturnT execute(Map<String, String> map, SchedulerTool tool) {
        invoiceApplyHeaderService.scheduleTask(map.get("delFlag"), map.get("applyStatus"), map.get("invoiceColor"), map.get("invoiceType"));
        return ReturnT.SUCCESS;
//        try {
//
//            //get data from invoice
//            InvoiceApplyHeader header =  new InvoiceApplyHeader();
//            List<InvoiceApplyHeader> failed = invoiceApplyHeaderService.failed(header);
//
//            //
//            InvoiceHeaderDTO header =  new InvoiceHeaderDTO();
//            header.setDelFlag(0);
//            header.setInvoiceColor("R");
//            header.setApplyStatus("F");
//            header.setInvoiceType("E");
////            List<InvoiceApplyHeader> failedInvoiceHeaders = invoiceApplyHeaderService.failed(header);
//            List<InvoiceHeaderDTO> invoiceApplyHeaders = invoiceApplyHeaderRepository.selectList(header);
//            //if no data
//            if(invoiceApplyHeaders==null){
//                log.info("No failed invoice headers found.");
//                return ReturnT.SUCCESS;
//            }
//            //send to redis queue
//            //
//
//        }catch(Exception e) {
//            return ReturnT.FAILURE;
//        }
//        InvoiceApplyHeader header =  new InvoiceApplyHeader();
//        List<InvoiceApplyHeader> failedInvoiceHeaders = invoiceApplyHeaderService.failedInvoiceHeaders(header);
//
//        Long userId = DetailsHelper.getUserDetails().getUserId();
//        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
//        String employeeId = map.get("employeeId");
//
//        log.info("detail client : {}", DetailsHelper.getUserDetailsElseAnonymous());
//
//        if (failedInvoiceHeaders == null || failedInvoiceHeaders.isEmpty()) {
//            log.info("No failed invoice headers found.");
//            return ReturnT.SUCCESS;
//        }
//
//        Map<String, Object> messageContent = new HashMap<>();
//        messageContent.put("userId", userId);
//        messageContent.put("tenantId", tenantId);
//        messageContent.put("employeeId", employeeId);
//        messageContent.put("failedInvoiceHeaders", failedInvoiceHeaders);
//
//        String jsonMessage = objectMapper.writeValueAsString(messageContent);
//        redisQueueHelper.push("48209:IAH",jsonMessage);
//
//        return ReturnT.SUCCESS;
//    } catch (Exception e) {
//        return ReturnT.FAILURE;
//    }
//    }
    }
}