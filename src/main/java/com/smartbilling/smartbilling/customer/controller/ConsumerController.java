package com.smartbilling.smartbilling.customer.controller;

import com.smartbilling.smartbilling.customer.dto.requests.ConsumerRequest;
import com.smartbilling.smartbilling.customer.dto.responses.ConsumerResponse;
import com.smartbilling.smartbilling.customer.repository.ConsumerRepository;
import com.smartbilling.smartbilling.customer.service.ConsumerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumers")
@RequiredArgsConstructor
public class ConsumerController {
    private final ConsumerService consumerService;
    private final ConsumerRepository consumerRepository;

    @PostMapping
    public ResponseEntity<ConsumerResponse> createConsumer(
            @Valid @RequestBody ConsumerRequest request) {

        ConsumerResponse response = consumerService.createConsumer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ConsumerResponse>> getAllConsumers() {
        return ResponseEntity.ok(consumerService.getAllConsumers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsumerResponse> getConsumerById(@PathVariable Long id) {
        return ResponseEntity.ok(consumerService.getConsumerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsumerResponse> updateConsumer(
            @PathVariable Long id,
            @Valid @RequestBody ConsumerRequest request) {

        return ResponseEntity.ok(consumerService.updateConsumer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsumer(@PathVariable Long id) {

        consumerService.deleteConsumer(id);
        return ResponseEntity.noContent().build();
    }

}
