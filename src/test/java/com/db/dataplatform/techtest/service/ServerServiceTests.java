package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.DataLakeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.db.dataplatform.techtest.TestDataHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DataLakeService dataLakeService;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;

    private Server server;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        ModelMapper mapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = mapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);

        DataHeaderEntity expectedDataHeaderEntity = mapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(expectedDataHeaderEntity);

        when(modelMapper.map(any(DataBody.class), ArgumentMatchers.<Class<DataBodyEntity>>any())).thenReturn(expectedDataBodyEntity);
        when(modelMapper.map(any(DataHeader.class), ArgumentMatchers.<Class<DataHeaderEntity>>any())).thenReturn(expectedDataHeaderEntity);

        doNothing().when(dataLakeService).saveDataEnvelope(any(DataEnvelope.class));
        server = new ServerImpl(dataBodyServiceImplMock, modelMapper, dataLakeService);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldNotSaveDataEnvelopeIfChecksumFails() throws NoSuchAlgorithmException, IOException {
        testDataEnvelope = createTestDataEnvelopeApiObjectWithWrongChecksum();
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isFalse();
        verify(dataBodyServiceImplMock, times(0)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldSaveDataEnvelopeIfChecksumCaseDoesNotMatch() throws NoSuchAlgorithmException, IOException {
        testDataEnvelope = createTestDataEnvelopeApiObjectWithLowercaseChecksum();
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }
}
