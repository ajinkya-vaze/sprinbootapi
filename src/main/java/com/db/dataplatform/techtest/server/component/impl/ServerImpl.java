package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.RecordNotFoundException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.DataLakeService;
import com.db.dataplatform.techtest.server.util.MD5Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final ModelMapper modelMapper;
    private final DataLakeService dataLakeService;

    /**
     * Saves envelop in the database if checksum matches.
     *
     * @param envelope Data to be saved in the database
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope) {
        if (validateChecksum(envelope)) {
            // Save to persistence.
            persist(envelope);
            log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
            pushToDataLake(envelope);
            return true;
        }
        return false;
    }

    private void pushToDataLake(DataEnvelope envelope) {
        dataLakeService.saveDataEnvelope(envelope);
    }

    /**
     * Finds data by block type
     *
     * @param blockType Block type to be search in the database
     * @return List of DataEnvelop models containing data
     */
    @Override
    public List<DataEnvelope> getDataByBlockType(BlockTypeEnum blockType) {
        List<DataBodyEntity> dataBodyEntities = dataBodyServiceImpl.getDataByBlockType(blockType);
        return mapToDataEnvelop(dataBodyEntities);
    }

    /**
     * Updates block type by for given block name
     *
     * @param blockName Name of the block to be updated
     * @param blockType Block type to be updated
     * @return true if data is successfully updated in the database
     * @throws RecordNotFoundException if there is no record in the database with given blockName
     */
    @Override
    @Transactional
    public boolean updateBlockTypeByName(String blockName, BlockTypeEnum blockType) throws RecordNotFoundException {
        Optional<DataBodyEntity> dataBodyEntityOptional = dataBodyServiceImpl.getDataByBlockName(blockName);
        if (!dataBodyEntityOptional.isPresent()) {
            throw new RecordNotFoundException(String.format("Block with block name %s is not present on server.", blockName));
        }
        DataBodyEntity dataBodyEntity = dataBodyEntityOptional.get();
        dataBodyEntity.getDataHeaderEntity().setBlocktype(blockType);
        saveData(dataBodyEntity);
        log.info("Data updated successfully, data name: {} and block type: {}", blockName, blockType);
        return true;
    }

    private List<DataEnvelope> mapToDataEnvelop(List<DataBodyEntity> dataBodyEntities) {
        List<DataEnvelope> dataEnvelopes = dataBodyEntities.stream()
                .map(dataBodyEntity -> {
                    DataBody dataBody = modelMapper.map(dataBodyEntity, DataBody.class);
                    DataHeader dataHeader = modelMapper.map(dataBodyEntity.getDataHeaderEntity(), DataHeader.class);
                    return new DataEnvelope(dataHeader, dataBody);
                }).collect(Collectors.toList());
        return dataEnvelopes;
    }

    private boolean validateChecksum(DataEnvelope envelope) {
        String calculatedChecksum = MD5Util.getMd5Hash(envelope.getDataBody().getDataBody());
        return calculatedChecksum.equalsIgnoreCase(envelope.getDataHeader().getChecksum());
    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

}
