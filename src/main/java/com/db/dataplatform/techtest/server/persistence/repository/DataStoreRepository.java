package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataStoreRepository extends JpaRepository<DataBodyEntity, Long> {

    @Query("SELECT dbe from DataBodyEntity dbe where dbe.dataHeaderEntity.blocktype = :blockType")
    List<DataBodyEntity> findByBlockType(@Param("blockType") BlockTypeEnum blockType);

    @Query("SELECT dbe from DataBodyEntity dbe where dbe.dataHeaderEntity.name = :name")
    Optional<DataBodyEntity> findByBlockName(@Param("name") String blockName);
}
