package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataBody;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import static com.db.dataplatform.techtest.TestDataHelper.*;

public class TestClientDataHelper {
    public static DataEnvelope createTestDataEnvelopeApiObject() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA, DUMMY_DATA_MD5_CHECKSUM);
        return new DataEnvelope(dataHeader, dataBody);
    }
}
