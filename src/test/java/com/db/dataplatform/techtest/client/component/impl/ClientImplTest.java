package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientImplTest {

    @Mock
    private RestTemplate restTemplateMock;

    @InjectMocks
    private ClientImpl client;

    private DataEnvelope testDataEnvelope;


    @Before
    public void setUp() {
        testDataEnvelope = TestClientDataHelper.createTestDataEnvelopeApiObject();
    }

    @Test
    public void pushDataShouldWorkWhenHttpCallIsSuccessful() {
        when(restTemplateMock.postForObject(ClientImpl.URI_PUSHDATA, testDataEnvelope, Boolean.class)).thenReturn(true);
        boolean response = client.pushData(testDataEnvelope);
        assertTrue(response);
    }
}
