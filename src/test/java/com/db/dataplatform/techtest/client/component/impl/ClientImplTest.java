package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
}
