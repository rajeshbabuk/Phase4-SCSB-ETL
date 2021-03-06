package org.recap.repository;

import org.recap.model.jpa.HoldingsEntity;
import org.recap.repository.jpa.BaseRepository;


/**
 * Created by chenchulakshmig on 6/13/16.
 */
public interface HoldingsDetailsRepository extends BaseRepository<HoldingsEntity> {

    /**
     * Count by owning institution id long.
     *
     * @param owningInstitutionId the owning institution id
     * @return the long
     */
    Long countByOwningInstitutionId(Integer owningInstitutionId);

    HoldingsEntity findByOwningInstitutionHoldingsIdAndOwningInstitutionId(String owningInstitutionHoldingsId, Integer owningInstitutionId);

}
