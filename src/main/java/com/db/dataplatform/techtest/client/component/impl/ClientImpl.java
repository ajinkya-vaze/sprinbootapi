package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

    private final RestTemplate restTemplate;

    @Override
    public boolean pushData(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
        try {
            HttpEntity<DataEnvelope> request = new HttpEntity<>(dataEnvelope);
            boolean response = restTemplate.postForObject(URI_PUSHDATA, request, Boolean.class);
            log.info("Successfully pushed data {} to server. Checksum check {}.", dataEnvelope.getDataHeader().getName(), getChecksumStatus(response));
            return response;
        } catch (Exception rce) {
            log.error("Exception while pushing data {} to server", dataEnvelope.getDataHeader().getName(), rce);
            return false;
        }
    }

    private String getChecksumStatus(boolean passed) {
        return passed ? "Passed" : "Failed";
    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        try {
            List<DataEnvelope> responseData = restTemplate.getForObject(URI_GETDATA.expand(blockType), List.class);
            log.info("Successfully received response from server for block type {}", blockType);
            return responseData;
        } catch (Exception rce) {
            log.error("Exception while querying for data with header block type {}", blockType, rce);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        try {
            HttpEntity<DataEnvelope> request = new HttpEntity<>(null);
            boolean response = restTemplate.patchForObject(URI_PATCHDATA.expand(blockName, newBlockType), request, Boolean.class);
            log.info("Successfully updated blocktype to {} for block with name {}", newBlockType, blockName);
            return response;
        } catch (Exception rce) {
            log.error("Exception while updating blocktype to {} for block with name {}", newBlockType, blockName);
            return false;
        }
    }


}
