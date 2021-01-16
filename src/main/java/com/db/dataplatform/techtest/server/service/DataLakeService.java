package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;

public interface DataLakeService {
    void saveDataEnvelope(DataEnvelope dataEnvelope);
}
