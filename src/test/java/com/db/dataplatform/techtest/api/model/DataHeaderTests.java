package com.db.dataplatform.techtest.api.model;

import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.db.dataplatform.techtest.TestDataHelper.DUMMY_DATA_MD5_CHECKSUM;
import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DataHeaderTests {

    @Test
    public void assignDataHeaderFieldsShouldWorkAsExpected() {
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA, DUMMY_DATA_MD5_CHECKSUM);

        assertThat(dataHeader.getName()).isEqualTo(TEST_NAME);
        assertThat(dataHeader.getBlockType()).isEqualTo(BlockTypeEnum.BLOCKTYPEA);
        assertThat(dataHeader.getChecksum()).isEqualTo(DUMMY_DATA_MD5_CHECKSUM);
    }
}
