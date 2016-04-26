/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.importer.external.service;

import org.dspace.importer.external.datamodel.ImportRecord;
import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;
import org.dspace.importer.external.metadatamapping.MetadataFieldMapping;
import org.dspace.importer.external.metadatamapping.MetadatumDTO;
import org.dspace.importer.external.metadatamapping.contributor.MetadataContributor;
import org.dspace.importer.external.metadatamapping.service.GenerateQueryService;
import org.dspace.importer.external.service.other.Imports;
import org.dspace.importer.external.service.other.MetadataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.util.LinkedList;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 29 May 2015
 */
public abstract class AbstractImportMetadataSourceService<RecordType> extends MetadataSource implements Imports {
	protected GenerateQueryService generateQueryService = null;
	private MetadataFieldMapping<RecordType, MetadataContributor<RecordType>> metadataFieldMapping;

    /**
     * Retrieve the {@link GenerateQueryService}
     * @return A GenerateForQueryService object set to this class
     */
	public GenerateQueryService getGenerateQueryForItem() {
		return generateQueryService;
	}

    /**
     * Set the {@link GenerateQueryService} used to create a {@link org.dspace.importer.external.Query} for a DSpace {@link org.dspace.content.Item}
     * @param generateQueryForItem
     */

	public void setGenerateQueryForItem(GenerateQueryService generateQueryForItem) {
		this.generateQueryService = generateQueryForItem;
	}
    public AbstractImportMetadataSourceService(GenerateQueryService generateQueryService,
											   MetadataFieldMapping<RecordType, MetadataContributor<RecordType>> metadataFieldMapping)
	{
        this.generateQueryService = generateQueryService;
        this.metadataFieldMapping = metadataFieldMapping;
    }

	public MetadataFieldMapping<RecordType, MetadataContributor<RecordType>> getMetadataFieldMapping() {
		return metadataFieldMapping;
	}

    /**
     * Sets the MetadataFieldMapping to base the mapping of RecordType and
     * @param metadataFieldMapping
     */
	public void setMetadataFieldMapping(
			MetadataFieldMapping<RecordType, MetadataContributor<RecordType>> metadataFieldMapping) {
		this.metadataFieldMapping = metadataFieldMapping;
	}

	public ImportRecord transformSourceRecords(RecordType rt){
		 return new ImportRecord(new LinkedList<MetadatumDTO>(getMetadataFieldMapping().resultToDCValueMapping(rt)), getMetadataFieldMapping().getIdentifier(rt, getImportSource()));
	}

	public boolean supports(String identifier) {
		return identifier.startsWith(getImportSource());
	}

	public String retrieveId(String publicationId) {
		String[] parts = publicationId.split("\\|\\|\\|");
		if (parts.length > 1) {
			return parts[1];
		}
		return publicationId;
	}
}
