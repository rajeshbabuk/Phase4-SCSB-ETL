package org.recap.service.transmission.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.zipfile.ZipAggregationStrategy;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.model.export.DataDumpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * Created by premkb on 28/9/16.
 */
@Service
public class DataDumpFileSystemTranmissionService implements DataDumpTransmissionInterface {

    @Value("${" + PropertyKeyConstants.ETL_DATA_DUMP_DIRECTORY + "}")
    private String dumpDirectoryPath;

    @Autowired
    private CamelContext camelContext;

    /**
     * Returns true if transmission type is 'FileSystem' for data dump.
     * @param dataDumpRequest the data dump request
     * @return
     */
    @Override
    public boolean isInterested(DataDumpRequest dataDumpRequest) {
        return dataDumpRequest.getTransmissionType().equals(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM);
    }

    /**
     * Transmit data dump file to the specified path after completion.
     * @param routeMap the route map
     * @throws Exception
     */
    @Override
    public void transmitDataDump(Map<String, String> routeMap) throws Exception {
        String requestingInstitutionCode = routeMap.get(ScsbConstants.REQUESTING_INST_CODE);
        String dateTimeFolder = routeMap.get(ScsbConstants.DATETIME_FOLDER);
        String fileName = routeMap.get(ScsbConstants.FILENAME);
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:"+ dumpDirectoryPath + File.separator + requestingInstitutionCode + File.separator + dateTimeFolder + "?antInclude=*.xml")
                        .routeId(ScsbConstants.DATADUMP_ZIP_FILESYSTEM_ROUTE_ID)
                        .aggregate(new ZipAggregationStrategy())
                        .constant(true)
                        .completionFromBatchConsumer()
                        .eagerCheckCompletion()
                        .to("file:"+ dumpDirectoryPath + File.separator+"?fileName="+ requestingInstitutionCode +File.separator + dateTimeFolder + File.separator + fileName + ".zip")
                ;
            }
        });
    }
}
