package com.hand.demo.app.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.demo.app.service.InvoiceInfoQueueService;
import com.hand.demo.domain.entity.InvoiceInfoQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.core.redis.handler.IBatchQueueHandler;
import org.hzero.core.redis.handler.QueueHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@QueueHandler("invoice_48209")
public class InvoiceQueueListen implements IBatchQueueHandler {
    @Autowired
    InvoiceInfoQueueService invoiceInfoQueueService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void process(List<String> messages) {
        // Parse the message and create an InvoiceInfoQueue object to process it.
        InvoiceInfoQueue queue = new InvoiceInfoQueue();
        queue.setContent(messages);
        queue.setEmployeeId("48209");
        queue.setTenantId(0L);
        List<InvoiceInfoQueue> invoiceInfoQueues = new ArrayList<>();
        invoiceInfoQueues.add(queue);
        invoiceInfoQueueService.saveData(invoiceInfoQueues);
    }
}
