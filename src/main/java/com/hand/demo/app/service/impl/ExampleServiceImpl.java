package com.hand.demo.app.service.impl;

import com.hand.demo.api.dto.PrefixDTO;
import com.hand.demo.app.service.ExampleService;
import com.hand.demo.infra.mapper.ExampleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * ExampleServiceImpl
 */
@Service
public class ExampleServiceImpl implements ExampleService {
    @Autowired
    private ExampleMapper exampleMapper;

    @Override
    public List<PrefixDTO> getFileConfig(Long organizationId) {
        return exampleMapper.selectList(new PrefixDTO());
    }
}
