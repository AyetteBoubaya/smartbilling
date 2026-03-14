package com.smartbilling.smartbilling.customer.service;

import com.smartbilling.smartbilling.customer.domain.Consumer;
import com.smartbilling.smartbilling.customer.dto.requests.ConsumerRequest;
import com.smartbilling.smartbilling.customer.dto.responses.ConsumerResponse;

import java.util.List;

public interface ConsumerService {
    ConsumerResponse createConsumer(ConsumerRequest consumer);
    List<ConsumerResponse> getAllConsumers();

    ConsumerResponse getConsumerById(Long id);

    ConsumerResponse updateConsumer(Long id, ConsumerRequest request);

    void deleteConsumer(Long id);

}
