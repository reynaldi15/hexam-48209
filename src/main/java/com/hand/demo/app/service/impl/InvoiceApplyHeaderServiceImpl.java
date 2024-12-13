package com.hand.demo.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.app.service.InvoiceApplyLineService;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.RedisQueueHelper;
import org.hzero.core.util.Results;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author
 * @since 2024-12-06 08:07:07
 */
@Service
@Slf4j
public class InvoiceApplyHeaderServiceImpl implements InvoiceApplyHeaderService {
    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;
    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;
    @Autowired
    private InvoiceApplyLineService invoiceApplyLineService;
    @Autowired
    private LovAdapter lovAdapter;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private CodeRuleBuilder codeRuleBuilder;
    @Autowired
    private RedisQueueHelper redisQueueHelper;
    private static final Logger logger = LoggerFactory.getLogger(InvoiceApplyHeaderServiceImpl.class);
    @Override
    public Page<InvoiceHeaderDTO> selectList(PageRequest pageRequest, InvoiceHeaderDTO invoiceApplyHeader) {
        return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyHeaderRepository.selectList(invoiceApplyHeader));
    }
    //Detail and line page
    @Override
    @ProcessLovValue
    public InvoiceHeaderDTO detailAndLine(Long applyHeaderId) {
        String headerCacheKey = "48209:IAH:" + applyHeaderId;
        String cachedValue = redisHelper.strGet(headerCacheKey);

        if(StringUtils.isNotBlank(cachedValue)){
            // TODO  JSON.paraeObject(chan,class)
            return redisHelper.fromJson(cachedValue, InvoiceHeaderDTO.class);
        }else{
            InvoiceHeaderDTO header = invoiceApplyHeaderRepository.selectByPrimary(applyHeaderId);
            InvoiceApplyLine invoiceApplyLine = new InvoiceApplyLine();
            invoiceApplyLine.setApplyHeaderId(applyHeaderId);
            List<InvoiceApplyLine> invoiceApplyLines = invoiceApplyLineRepository.selectList(invoiceApplyLine);
            header.setInvoiceApplyLines(invoiceApplyLines);
            redisHelper.strSet(headerCacheKey, redisHelper.toJson(header));

            return header;
        }

    }
//    create and update data
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(List<InvoiceApplyHeader> invoiceApplyHeaders) {
        String headerCacheKey = "48209:IAH" + ":*";
        List<InvoiceApplyHeader> insert = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId()== null).collect(Collectors.toList());
        //Todo cahnge thelogic. dont looping befor query
        invoiceApplyHeaders.forEach(header -> validateData(header, lovAdapter));
        insert.forEach(this::generateNumber);
        invoiceApplyHeaderRepository.batchInsertSelective(insert);
        //Todo Update data
        lineProcess(invoiceApplyHeaders);
        redisHelper.delKey(headerCacheKey);

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


    private void generateNumber(InvoiceApplyHeader invoiceApplyHeader) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());
        String ruleCode = "HEXAM-INV-48209";
        // Todo delete the variable map and make null
        Map<String, String> variableMap = new HashMap<>();
        variableMap.put("DATE_PART", datePart);
        String applyHeaderNumber = codeRuleBuilder.generateCode(ruleCode, variableMap);
        invoiceApplyHeader.setApplyHeaderNumber(applyHeaderNumber);
    }
    // Todo change the name to invoiceApplyHeaderlist    CollUtil.isEmpty(invoiceApplyHeader)
    public void lineProcess(List<InvoiceApplyHeader> invoiceApplyHeader){
        if (invoiceApplyHeader == null || invoiceApplyHeader.isEmpty()) {
            return;
        }
        List<InvoiceApplyLine> listline = new ArrayList<>();
        for (InvoiceApplyHeader header : invoiceApplyHeader) {
            if (header == null) {
                continue;
            }
            List<InvoiceApplyLine> lines = header.getInvoiceApplyLines();
            if (lines != null) {
                for (InvoiceApplyLine line : lines) {
                    if (line != null && header.getApplyHeaderId() != null) {
                        line.setApplyHeaderId(header.getApplyHeaderId());
                        listline.add(line);
                    }
                }
            }
        }

        if (!listline.isEmpty()) {
            invoiceApplyLineService.saveData(listline);
        }

    }

//    batas
@Override
public ResponseEntity<InvoiceApplyHeader> deleteById(Long applyHeaderId) {
    InvoiceApplyHeader invoiceApplyHeader = invoiceApplyHeaderRepository.selectByPrimaryKey(applyHeaderId);
    if (invoiceApplyHeader != null) {
        invoiceApplyHeader.setDelFlag(1);
        invoiceApplyHeaderRepository.updateByPrimaryKeySelective(invoiceApplyHeader);
//        invoiceApplyHeaderRepository.updateOptional(invoiceApplyHeader, InvoiceApplyHeader.FIELD_DEL_FLAG);
        deleteRedisCache(invoiceApplyHeader);
    }
    return Results.success(invoiceApplyHeader);
}

    private void deleteRedisCache(InvoiceApplyHeader invoiceApplyHeader) {
        String headerCacheKey = "48209:IAH" + invoiceApplyHeader.getApplyHeaderId();
        redisHelper.delKey(headerCacheKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ProcessLovValue
    public List<InvoiceHeaderDTO> exportHeader(InvoiceApplyHeader invoiceApplyHeader){
        try {
            List<InvoiceApplyHeader> headers = invoiceApplyHeaderRepository.selectList(invoiceApplyHeader);
            List<InvoiceHeaderDTO> exportList = new ArrayList<>();

            for(InvoiceApplyHeader header : headers) {
                InvoiceHeaderDTO exportHeader = new InvoiceHeaderDTO();
                BeanUtils.copyProperties(header, exportHeader);
                validateData(exportHeader, lovAdapter);
                exportList.add(exportHeader);
            }
            return exportList;
        } catch (Exception e) {
            throw new IllegalStateException (e);
        }
    }
    @Override
    @Transactional
    public List<InvoiceApplyHeader> failed(InvoiceApplyHeader invoiceApplyHeader) {
        invoiceApplyHeader.setDelFlag(0);
        invoiceApplyHeader.setInvoiceColor("R");
        invoiceApplyHeader.setApplyStatus("F");
        invoiceApplyHeader.setInvoiceType("E");
        return invoiceApplyHeaderRepository.selectList(invoiceApplyHeader);
    }
    // scheduler
    @Override
    public void scheduleTask(String delFlag, String applyStatus, String invoiceColor, String invoiceType){
        List<InvoiceApplyHeader> invoiceApplyHeaders = invoiceApplyHeaderRepository.selectList(new InvoiceApplyHeader() {{
            setDelFlag(Integer.parseInt(delFlag));
            setApplyStatus(applyStatus);
            setInvoiceColor(invoiceColor);
            setInvoiceType(invoiceType);
        }});
        if (invoiceApplyHeaders.isEmpty()) {
            log.info("InvoiceApplyHeaders is empty for scheduling task");
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
// Convert each InvoiceApplyHeader to JSON string and collect into a List
        try {
            // Convert list to JSON string
            String jsonString = objectMapper.writeValueAsString(invoiceApplyHeaders);
            // Save to Redis Message Queue
            String redisKey = "Hexam-48208:Exam";
            redisQueueHelper.push(redisKey, jsonString);
        } catch (JsonProcessingException e) {
            throw new CommonException("Error converting list to JSON");
        }



    }


}
