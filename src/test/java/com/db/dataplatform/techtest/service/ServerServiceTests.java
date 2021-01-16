package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.exception.RecordNotFoundException;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.DataLakeService;
import org.assertj.core.internal.cglib.core.Block;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public void shouldSaveDataEnvelopeAsExpected() {
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldNotSaveDataEnvelopeIfChecksumFails() {
        testDataEnvelope = createTestDataEnvelopeApiObjectWithWrongChecksum();
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isFalse();
        verify(dataBodyServiceImplMock, times(0)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldSaveDataEnvelopeIfChecksumCaseDoesNotMatch() {
        testDataEnvelope = createTestDataEnvelopeApiObjectWithLowercaseChecksum();
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void getDataByBlockTypeShouldMapDataToDataEnvelope() {
        DataBodyEntity dataBodyEntity = createTestDataBodyEntity();
        when(dataBodyServiceImplMock.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(Arrays.asList(dataBodyEntity));

        when(modelMapper.map(any(DataBodyEntity.class),  ArgumentMatchers.<Class<DataBody>>any())).thenReturn(testDataEnvelope.getDataBody());
        when(modelMapper.map(any(DataHeaderEntity.class), ArgumentMatchers.<Class<DataHeader>>any())).thenReturn(testDataEnvelope.getDataHeader());
        List<DataEnvelope> dataEnvelopes = server.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);
        DataEnvelope dataEnvelope = dataEnvelopes.get(0);

        Assert.assertEquals(dataBodyEntity.getDataBody(), dataEnvelope.getDataBody().getDataBody());
        Assert.assertEquals(dataBodyEntity.getDataHeaderEntity().getName(), dataEnvelope.getDataHeader().getName());
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateBlockTypeByNameThrowsExceptionWhenDataNotFound() throws RecordNotFoundException {
        when(dataBodyServiceImplMock.getDataByBlockName(TEST_NAME)).thenReturn(Optional.empty());
        server.updateBlockTypeByName(TEST_NAME, BlockTypeEnum.BLOCKTYPEB);
    }

    @Test
    public void updateBlockTypeByNameUpdatesDataInTheRepository() throws RecordNotFoundException {
        DataBodyEntity dataBodyEntity = createTestDataBodyEntity();
        when(dataBodyServiceImplMock.getDataByBlockName(TEST_NAME)).thenReturn(Optional.of(dataBodyEntity));
        server.updateBlockTypeByName(TEST_NAME, BlockTypeEnum.BLOCKTYPEB);
        verify(dataBodyServiceImplMock, times(1))
                .saveDataBody(eq(dataBodyEntity));
    }
}
