package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.RecordNotFoundException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriTemplate;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerComponentTest {

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

    @Mock
    private Server serverMock;

    private DataEnvelope testDataEnvelope;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private ServerController serverController;

    @Before
    public void setUp() {
        serverController = new ServerController(serverMock);
        mockMvc = standaloneSetup(serverController).build();
        objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .build();

        testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

    }

    @Test
    public void testPushDataPostCallWorksAsExpected() throws Exception {
        String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

        when(serverMock.saveDataEnvelope(any(DataEnvelope.class))).thenReturn(true);
        MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
                .content(testDataEnvelopeJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
        assertThat(checksumPass).isTrue();
    }

    @Test
    public void testGetDataByBlockTypeReturnsAppropriateData() throws Exception {
        when(serverMock.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(Arrays.asList(testDataEnvelope));
        mockMvc.perform(get(URI_GETDATA.expand(BlockTypeEnum.BLOCKTYPEA.name())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].dataHeader.name", is(testDataEnvelope.getDataHeader().getName())));
    }

    @Test
    public void testUpdateBlockTypeByBlockName() throws Exception {
        when(serverMock.updateBlockTypeByName(testDataEnvelope.getDataHeader().getName(), BlockTypeEnum.BLOCKTYPEA)).thenReturn(true);
        MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand(testDataEnvelope.getDataHeader().getName(), BlockTypeEnum.BLOCKTYPEA.name())))
                .andExpect(status().isOk())
                .andReturn();

        boolean updateSuccessful = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
        assertThat(updateSuccessful).isTrue();
    }

    @Test
    public void testUpdateBlockTypeByBlockNameValidatesBlockName() throws Exception {
        when(serverMock.updateBlockTypeByName(testDataEnvelope.getDataHeader().getName(), BlockTypeEnum.BLOCKTYPEA)).thenThrow(new RecordNotFoundException("Data not found."));
        mockMvc.perform(patch(URI_PATCHDATA.expand(testDataEnvelope.getDataHeader().getName(), BlockTypeEnum.BLOCKTYPEA.name())))
                .andExpect(status().is4xxClientError());
    }
}
