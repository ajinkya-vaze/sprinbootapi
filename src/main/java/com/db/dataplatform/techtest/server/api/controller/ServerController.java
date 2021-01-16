package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.RecordNotFoundException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope);

        log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        return ResponseEntity.ok(checksumPass);
    }

    @GetMapping("/data/{blockType}")
    public ResponseEntity<List<DataEnvelope>> getDataByBlockType(@PathVariable BlockTypeEnum blockType) {
        log.info("Query request received for type {}", blockType);
        List<DataEnvelope> data = server.getDataByBlockType(blockType);
        return ResponseEntity.ok(data);
    }

    @PatchMapping("/update/{name}/{newBlockType}")
    public ResponseEntity<Boolean> updateBlockTypeByBlockName(@PathVariable String name, @PathVariable BlockTypeEnum newBlockType) {
        log.info("Received request to update block with name {} to block type {}", name, newBlockType);
        try {
            boolean updateSuccessful = server.updateBlockTypeByName(name, newBlockType);
            return ResponseEntity.ok(updateSuccessful);
        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
