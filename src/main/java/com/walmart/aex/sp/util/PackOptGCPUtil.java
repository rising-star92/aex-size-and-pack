package com.walmart.aex.sp.util;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PackOptGCPUtil {
    @ManagedConfiguration
    BigQueryConnectionProperties bigQueryConnectionProperties;
    private final String projectId = bigQueryConnectionProperties.getMLProjectId();
    private final String bucketName = bigQueryConnectionProperties.getMLDataSetName();

    public void deleteMultiBumpPackDataSet(Long planId, Integer finelineNbr, String env) {
        try {
            String storagePathInput = env + "/input/" + planId;
            String storagePathOutput = env + "/output/" + planId;
            String multiBumpSetInputFolderPrefix = storagePathInput + '/' + finelineNbr + "_BP";
            String multiBumpSetOutputFolderPrefix = storagePathOutput + '/' + finelineNbr + "_BP";
            boolean isDeletedAllInputFolders = delete(storagePathInput, multiBumpSetInputFolderPrefix, planId.toString(), finelineNbr.toString());
            boolean isdDeletedAllOutputFolders = delete(storagePathOutput, multiBumpSetOutputFolderPrefix, planId.toString(), finelineNbr.toString());

            if (isDeletedAllInputFolders && isdDeletedAllOutputFolders) {
                log.info("Bump Pack dataset cleanup for planId {} and finelineNbr {} completed successfully !", planId, finelineNbr);
            } else {
                log.info("Bump Pack dataset cleanup for planId {} and finelineNbr {} not completed successfully !", planId, finelineNbr);
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting GCP bump pack dataset for planId {} and finelineNbr {}. Exception: ", planId, finelineNbr, e);
        }

    }

    private boolean delete(String storagePath, String multiBumpSetFolderPrefix, String planId, String finelineNbr) {
        boolean deleted = false;
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        Page<Blob> blobs =
                storage.list(
                        bucketName,
                        Storage.BlobListOption.prefix(storagePath));

        for (Blob blob : blobs.iterateAll()) {
            if (blob.getName().startsWith(multiBumpSetFolderPrefix)) {
                deleted = blob.delete(Blob.BlobSourceOption.generationMatch());
                if (deleted) {
                    log.info("Deleted Bump pack dataset for planId {} and finelineNbr {} : gs://{}", planId, finelineNbr, bucketName + '/' + blob.getName());
                }
            }
        }
        return deleted;
    }

}
