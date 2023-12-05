package com.walmart.aex.sp.util;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.walmart.aex.sp.dto.currentlineplan.LikeAssociation;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommonGCPUtil {
    @ManagedConfiguration
    private BigQueryConnectionProperties bigQueryConnectionProperties;
    public boolean delete(String storagePath, String folderPrefix) {
        boolean deleted = false;
        final String projectId = bigQueryConnectionProperties.getMLProjectId();
        final String bucketName = bigQueryConnectionProperties.getMLDataSetName();
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        Page<Blob> blobs =
                storage.list(
                        bucketName,
                        Storage.BlobListOption.prefix(storagePath));
        for (Blob blob : blobs.iterateAll()) {
            if (blob.getName().startsWith(folderPrefix)) {
                deleted = blob.delete(Blob.BlobSourceOption.generationMatch());
                if (deleted) {
                    log.info("Deleted dataset: gs://{}", bucketName + '/' + blob.getName());
                }
            }
        }
        return deleted;
    }

    public static String getFinelineVolumeClusterQuery(String finelineVolClusterTable, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String season, Integer fiscalYear, LikeAssociation likeAssociation) {
        return "SELECT\n" +
                "  store_nbr AS store,\n" +
                "  cluster_id AS clusterId\n" +
                "FROM\n" +
                "  `" + finelineVolClusterTable + "`\n" +
                "WHERE\n" +
                "  season = '" + season + "'\n" +
                "  AND fiscal_year = " + fiscalYear + "\n" +
                (null != likeAssociation && null != likeAssociation.getId() ?
                        "  AND dept_catg_nbr = " + likeAssociation.getLvl3Nbr() + "\n" +
                        "  AND dept_subcatg_nbr = " + likeAssociation.getLvl4Nbr() + "\n" +
                        "  AND fineline_nbr = " + likeAssociation.getId() + "\n"
                                :
                        "  AND dept_catg_nbr = " + lvl3Nbr + "\n" +
                        "  AND dept_subcatg_nbr = " + lvl4Nbr + "\n" +
                        "  AND fineline_nbr = " + finelineNbr + "\n") ;
    }
}
