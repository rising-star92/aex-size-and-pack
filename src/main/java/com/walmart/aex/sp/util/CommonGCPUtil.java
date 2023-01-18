package com.walmart.aex.sp.util;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommonGCPUtil {
    @ManagedConfiguration
    BigQueryConnectionProperties bigQueryConnectionProperties;
    private final String projectId = bigQueryConnectionProperties.getMLProjectId();
    private final String bucketName = bigQueryConnectionProperties.getMLDataSetName();

    public boolean delete(String storagePath, String folderPrefix) {
        boolean deleted = false;
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

}
