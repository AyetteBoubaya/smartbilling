package com.smartbilling.smartbilling.customer.service;

import com.smartbilling.smartbilling.customer.domain.Consumer;
import com.smartbilling.smartbilling.customer.dto.requests.ConsumerRequest;
import com.smartbilling.smartbilling.customer.dto.responses.ConsumerResponse;
import com.smartbilling.smartbilling.customer.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final ConsumerRepository consumerRepository;

    @Override
    public ConsumerResponse createConsumer(ConsumerRequest request) {
        if (consumerRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already exists");
        }
        Consumer consumer = new Consumer();
        consumer.setName(request.name());
        consumer.setEmail(request.email());
        consumer.setAddress(request.address());

        Consumer saved = consumerRepository.save(consumer);

        return new ConsumerResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getAddress(),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<ConsumerResponse> getAllConsumers() {
        return consumerRepository.findAll()
                .stream()
                .map(consumer -> new ConsumerResponse(
                        consumer.getId(),
                        consumer.getName(),
                        consumer.getEmail(),
                        consumer.getAddress(),
                        consumer.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public ConsumerResponse getConsumerById(Long id) {

        Consumer consumer = consumerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumer not found"));

        return new ConsumerResponse(
                consumer.getId(),
                consumer.getName(),
                consumer.getEmail(),
                consumer.getAddress(),
                consumer.getCreatedAt()
        );
    }

    @Override
    public void deleteConsumer(Long id) {
        Consumer existing = consumerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumer not found"));

        consumerRepository.delete(existing);
    }

    @Override
    public ConsumerResponse updateConsumer(Long id, ConsumerRequest request) {

        Consumer existing = consumerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumer not found"));

        existing.setName(request.name());
        existing.setEmail(request.email());
        existing.setAddress(request.address());

        Consumer updated = consumerRepository.save(existing);

        return new ConsumerResponse(
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                updated.getAddress(),
                updated.getCreatedAt()
        );
    }
}

