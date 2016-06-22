package org.recap.model;

import org.recap.model.jaxb.Bib;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.BibliographicHoldingsEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.util.MarcUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pvsubrah on 6/21/16.
 */
public class BibliographicEntityGenerator {

    private InstitutionDetailsRepository institutionDetailsRepository;
    private MarcUtil marcUtil;

    public InstitutionDetailsRepository getInstitutionDetailsRepository() {
        return institutionDetailsRepository;
    }

    public void setInstitutionDetailsRepository(InstitutionDetailsRepository institutionDetailsRepository) {
        this.institutionDetailsRepository = institutionDetailsRepository;
    }

    public List<BibliographicHoldingsEntity> generateBibliographicEntity(BibRecord bibRecord){

        List<BibliographicHoldingsEntity> bibliographicHoldingsEntityList = new ArrayList<>();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();

        bibliographicEntity.setCreatedDate(new Date());
        Bib bib = bibRecord.getBib();
        bibliographicEntity.setOwningInstitutionBibId(null == bib.getOwningInstitutionBibId() ? getControlFieldValue001(bibRecord) : bib.getOwningInstitutionBibId());
        bibliographicEntity.setOwningInstitutionId(institutionDetailsRepository.findByInstitutionCode(bib.getOwningInstitutionId()).getInstitutionId());
        bibliographicEntity.setCreatedDate(new Date());
        ContentType content = bib.getContent();

        CollectionType collection = content.getCollection();
        String xmlContent = collection.serialize(collection);
        bibliographicEntity.setContent(xmlContent);

        List<Holdings> holdings = bibRecord.getHoldings();
        for (Iterator<Holdings> iterator = holdings.iterator(); iterator.hasNext(); ) {
            Holdings holdingsList = iterator.next();
            List<Holding> holding = holdingsList.getHolding();
            for (Iterator<Holding> holdingIterator = holding.iterator(); holdingIterator.hasNext(); ) {
                BibliographicHoldingsEntity bibliographicHoldingsEntity = new BibliographicHoldingsEntity();
                Holding holdingEnt = holdingIterator.next();
                HoldingsEntity holdingsEntity = new HoldingsEntity();
                CollectionType holdingCollection = holdingEnt.getContent().getCollection();
                holdingsEntity.setContent(holdingCollection.serialize(holdingCollection));
                holdingsEntity.setCreatedDate(new Date());

                bibliographicHoldingsEntity.setBibliographicEntity(bibliographicEntity);
                bibliographicHoldingsEntity.setHoldingsEntity(holdingsEntity);

                bibliographicHoldingsEntityList.add(bibliographicHoldingsEntity);
            }
        }

        return bibliographicHoldingsEntityList;
    }

    private String getControlFieldValue001(BibRecord bibRecord) {
        RecordType marcRecord = bibRecord.getBib().getContent().getCollection().getRecord().get(0);
        return getMarcUtil().getControlFieldValue(marcRecord, "001");
    }

    public MarcUtil getMarcUtil() {
        if (null == marcUtil) {
            marcUtil = new MarcUtil();
        }
        return marcUtil;
    }

    public void setMarcUtil(MarcUtil marcUtil) {
        this.marcUtil = marcUtil;
    }
}
