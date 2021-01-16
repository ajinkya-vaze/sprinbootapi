package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.RecordNotFoundException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.util.List;

public interface Server {
    boolean saveDataEnvelope(DataEnvelope envelope);

    List<DataEnvelope> getDataByBlockType(BlockTypeEnum blockType);

    boolean updateBlockTypeByName(String blockName, BlockTypeEnum blockType) throws RecordNotFoundException;
}
