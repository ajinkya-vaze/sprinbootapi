package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    private ClientImpl client;

    private DataEnvelope testDataEnvelope;

    @Before
    public void setUp() {
        client = new ClientImpl(restTemplate);
        testDataEnvelope = TestClientDataHelper.createTestDataEnvelopeApiObject();
    }

    @Test
    public void pushDataShouldWorkWhenHttpCallIsSuccessful() {
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any())).thenReturn(true);
        boolean response = client.pushData(testDataEnvelope);
        assertTrue(response);
    }

    @Test
    public void pushDataShouldReturnChecksumStatus() {
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any())).thenReturn(false);
        boolean response = client.pushData(testDataEnvelope);
        assertFalse(response);
    }

    @Test
    public void pushDataReturnsFalseWhenHttpCallResultsInClientException() {
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        boolean response = client.pushData(testDataEnvelope);
        assertFalse(response);
    }

    @Test
    public void pushDataReturnsFalseWhenHttpCallResultsInServerException() {
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any())).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        boolean response = client.pushData(testDataEnvelope);
        assertFalse(response);
    }

    @Test
    public void pushDataReturnsFalseWhenRestClientFails() {
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any())).thenThrow(new RestClientException("Exception while creating the request"));
        boolean response = client.pushData(testDataEnvelope);
        assertFalse(response);
    }

    @Test
    public void getDataShouldWorkWhenApiCallIsSuccessful() {
        List<DataEnvelope> expectedData = Arrays.asList(testDataEnvelope);
        when(restTemplate.getForObject(any(URI.class), ArgumentMatchers.<Class<List>>any())).thenReturn(expectedData);
        List<DataEnvelope> actualData = client.getData(BlockTypeEnum.BLOCKTYPEA.name());
        assertEquals(expectedData, actualData);
    }

    @Test
    public void getDataReturnEmptyListWhenApiCallFailsWithInvalidInput() {
        when(restTemplate
                .getForObject(any(URI.class), ArgumentMatchers.<Class<List>>any())
        ).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        List<DataEnvelope> responseData = client.getData(BlockTypeEnum.BLOCKTYPEA.name());
        assertEquals(0, responseData.size());
    }

    @Test
    public void getDataReturnEmptyListWhenApiCallFailsWithInvalidResponseFromServer() {
        when(restTemplate
                .getForObject(any(URI.class), ArgumentMatchers.<Class<List>>any())
        ).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        List<DataEnvelope> responseData = client.getData(BlockTypeEnum.BLOCKTYPEA.name());
        assertEquals(0, responseData.size());
    }

    @Test
    public void getDataReturnEmptyListWhenApiCallFails() {
        when(restTemplate
                .getForObject(any(URI.class), ArgumentMatchers.<Class<List>>any())
        ).thenThrow(new RestClientException("Exception while creating the request"));
        List<DataEnvelope> responseData = client.getData(BlockTypeEnum.BLOCKTYPEA.name());
        assertEquals(0, responseData.size());
    }

    @Test
    public void updateDataShouldWorkWhenApiCallIsSuccessful() {
        when(restTemplate.patchForObject(any(URI.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any())).thenReturn(true);
        boolean response = client.updateData(TEST_NAME, BlockTypeEnum.BLOCKTYPEB.name());
        assertTrue(response);
    }

    @Test
    public void updateDataReturnsFalseWhenHttpCallResultsInClientException() {
        when(restTemplate.patchForObject(any(URI.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        boolean response = client.updateData(TEST_NAME, BlockTypeEnum.BLOCKTYPEB.name());
        assertFalse(response);
    }

    @Test
    public void updateDataReturnsFalseWhenHttpCallResultsInServerException() {
        when(restTemplate.patchForObject(any(URI.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        boolean response = client.updateData(TEST_NAME, BlockTypeEnum.BLOCKTYPEB.name());
        assertFalse(response);
    }

    @Test
    public void updateDataReturnsFalseRestClientFails() {
        when(restTemplate.patchForObject(any(URI.class), any(HttpEntity.class), ArgumentMatchers.<Class<Boolean>>any()))
                .thenThrow(new RestClientException("Exception while updating the data"));
        boolean response = client.updateData(TEST_NAME, BlockTypeEnum.BLOCKTYPEB.name());
        assertFalse(response);

    }
}
