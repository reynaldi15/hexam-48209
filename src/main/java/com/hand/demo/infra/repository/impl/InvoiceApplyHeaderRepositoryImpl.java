package com.hand.demo.infra.repository.impl;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import com.hand.demo.infra.mapper.InvoiceApplyLineMapper;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ErrorMessages;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.infra.mapper.InvoiceApplyHeaderMapper;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.*;

/**
 * (InvoiceApplyHeader)资源库
 *
 * @author
 * @since 2024-12-06 08:07:06
 */
@Component
public class InvoiceApplyHeaderRepositoryImpl extends BaseRepositoryImpl<InvoiceApplyHeader> implements InvoiceApplyHeaderRepository {
    private static final Logger log = LoggerFactory.getLogger(InvoiceApplyHeaderRepositoryImpl.class);
    @Resource
    private InvoiceApplyHeaderMapper invoiceApplyHeaderMapper;
    @Resource
    private InvoiceApplyLineMapper invoiceApplyLineMapper;
    @Resource
    private InvoiceApplyLineRepository invoiceApplyLineRepository;

    @Override
    public List<InvoiceHeaderDTO> selectList(InvoiceHeaderDTO invoiceApplyHeader) {
        return invoiceApplyHeaderMapper.selectList(invoiceApplyHeader);
    }
//    Todo optimize the code with close the programe dosn't use
    @Override
    public List<InvoiceApplyHeader> selectList(InvoiceApplyHeader invoiceApplyHeader) {
        return invoiceApplyHeaderMapper.selectList(invoiceApplyHeader);
    }

    @Override
    public InvoiceHeaderDTO selectByPrimary(Long applyHeaderId) {
        InvoiceHeaderDTO invoiceApplyHeader = new InvoiceHeaderDTO();
        invoiceApplyHeader.setApplyHeaderId(applyHeaderId);
        List<InvoiceHeaderDTO> invoiceApplyHeaders = invoiceApplyHeaderMapper.selectList(invoiceApplyHeader);
        log.info("inv : {}", invoiceApplyHeaders);
        if (invoiceApplyHeaders.size() == 0) {
            return null;
        }
        return invoiceApplyHeaders.get(0);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void softDelete(List<InvoiceApplyHeader> invoiceApplyHeaders) {
        // Melakukan iterasi terhadap setiap objek InvoiceApplyHeader dalam daftar invoiceApplyHeaders
        for (InvoiceApplyHeader invoiceApplyHeader : invoiceApplyHeaders) {
            // Menandai InvoiceApplyHeader sebagai "dihapus" dengan mengubah status DelFlag menjadi 1
            // Biasanya, DelFlag ini digunakan untuk menandai apakah data tersebut sudah dihapus secara logis (soft delete) atau tidak
            invoiceApplyHeader.setDelFlag(1);
            // Memperbarui entri InvoiceApplyHeader di database dengan nilai DelFlag yang telah diubah
            // updateByPrimaryKeySelective hanya akan mengupdate kolom yang diset, dalam hal ini DelFlag saja
            updateByPrimaryKeySelective(invoiceApplyHeader);
            //Todo change the logic in the controller and use updateoptional or batchupdate
        }
    }
    @Override
    public InvoiceApplyHeader selectApplyHeaderNumber(String applyHeaderNumber) {
//        Todo close to try code
//        return invoiceApplyHeaderMapper.selectHeaderNumber(applyHeaderNumber);
        return null;
    }

}



