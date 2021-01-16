package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.service.DataLakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLakeServiceImpl implements DataLakeService {
    private static final String DATA_LAKE_PUSH_DATA_URL = "http://localhost:8090/hadoopserver/pushbigdata";
    private final RestTemplate restTemplate;

    @Override
    @Async
    public void saveDataEnvelope(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {} ", dataEnvelope.getDataHeader().getName(), DATA_LAKE_PUSH_DATA_URL);
        try {
            HttpEntity<DataEnvelope> request = new HttpEntity<>(dataEnvelope);
            ResponseEntity<HttpStatus> response = restTemplate.postForEntity(DATA_LAKE_PUSH_DATA_URL, request, HttpStatus.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully pushed data {} to data lake.", dataEnvelope.getDataHeader().getName());
            }
        } catch (Exception e) {
            log.error("Exception while pushing data {} to data lake.", dataEnvelope.getDataHeader().getName(), e);
        }
    }
}
