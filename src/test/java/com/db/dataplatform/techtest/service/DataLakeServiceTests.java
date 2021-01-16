package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.service.DataLakeService;
import com.db.dataplatform.techtest.server.service.impl.DataLakeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataLakeServiceTests {
    @Mock
    private RestTemplate restTemplate;

    private DataLakeService dataLakeService;

    private DataEnvelope dataEnvelope;

    @Before
    public void setup() {
        dataLakeService = new DataLakeServiceImpl(restTemplate);
        dataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();
    }

    @Test
    public void dataLakeServicePostsDataToDataLakePushEndpoint() {
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), ArgumentMatchers.<Class<HttpStatus>>any())).thenReturn(ResponseEntity.ok(HttpStatus.OK));
        dataLakeService.saveDataEnvelope(dataEnvelope);
        verify(restTemplate, times(1)).postForEntity(any(String.class), any(HttpEntity.class), ArgumentMatchers.<Class<HttpStatus>>any());
    }

    @Test
    public void dataLaeServiceHandlesExceptions() {
        try {
            when(restTemplate
                    .postForEntity(
                            any(String.class),
                            any(HttpEntity.class),
                            ArgumentMatchers.<Class<HttpStatus>>any()))
                    .thenThrow(new RestClientException("Request failed"));
            dataLakeService.saveDataEnvelope(dataEnvelope);
        } catch (Exception e) {
            fail("Exception should be handled by dataLakeService.saveDataEnvelope method");
        }
    }
}
