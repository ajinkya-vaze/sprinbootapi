package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.time.Instant;

public class TestDataHelper {

    public static final String TEST_NAME = "Test";
    public static final String TEST_NAME_EMPTY = "";
    public static final String DUMMY_DATA = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";
    public static final String DUMMY_DATA_MD5_CHECKSUM = "CECFD3953783DF706878AAEC2C22AA70";

    public static DataHeaderEntity createTestDataHeaderEntity(Instant expectedTimestamp) {
        DataHeaderEntity dataHeaderEntity = new DataHeaderEntity();
        dataHeaderEntity.setName(TEST_NAME);
        dataHeaderEntity.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity.setCreatedTimestamp(expectedTimestamp);
        return dataHeaderEntity;
    }

    public static DataBodyEntity createTestDataBodyEntity(DataHeaderEntity dataHeaderEntity) {
        DataBodyEntity dataBodyEntity = new DataBodyEntity();
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        dataBodyEntity.setDataBody(DUMMY_DATA);
        return dataBodyEntity;
    }

    public static DataEnvelope createTestDataEnvelopeApiObject() {
        DataBody dataBody = createDataBody();
        DataHeader dataHeader = createDataHeader(TEST_NAME, DUMMY_DATA_MD5_CHECKSUM);

        return new DataEnvelope(dataHeader, dataBody);
    }

    private static DataHeader createDataHeader(String testName, String checksum) {
        return new DataHeader(testName, BlockTypeEnum.BLOCKTYPEA, checksum);
    }

    private static DataBody createDataBody() {
        return new DataBody(DUMMY_DATA);
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithWrongChecksum() {
        DataBody dataBody = createDataBody();
        DataHeader dataHeader = createDataHeader(TEST_NAME, "testChecksum");

        return new DataEnvelope(dataHeader, dataBody);
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithLowercaseChecksum() {
        DataBody dataBody = createDataBody();
        DataHeader dataHeader = createDataHeader(TEST_NAME, DUMMY_DATA_MD5_CHECKSUM.toLowerCase());

        return new DataEnvelope(dataHeader, dataBody);
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithEmptyName() {
        DataBody dataBody = createDataBody();
        DataHeader dataHeader = createDataHeader(TEST_NAME_EMPTY, DUMMY_DATA_MD5_CHECKSUM);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }
}
