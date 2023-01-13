package com.walmart.aex.sp.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.walmart.aex.sp.dto.StatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class PackOptUtilityService {
    // The ID of your GCP project
    String projectId = "wmt-mtech-assortment-ml-prod";
//                 bigQueryConnectionProperties.getMLProjectId();;

    // The ID of your GCS bucket
    String bucketName = "aex_pack_opt_non_prod";

    // The ID of your GCS object
    String objectName = "test";
    public StatusResponse uploadFile(String fileName) {

        try{

            BlobId blobId = BlobId.of(bucketName,fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            File fileToRead = new File("/Users/d0o03d1/Documents/workspace/API-SPO/aex-size-and-pack/src/main/resources/",fileName);
            byte[] data = Files.readAllBytes(Paths.get(fileToRead.toURI()));
            Storage storage =
                    StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            storage.create(blobInfo,data);

            StatusResponse message = new StatusResponse();
            message.setMessage(new String(data));
            return message;

        }catch (Exception e){
            log.error("An error occurred while uploading data. Exception: ", e);
        }
        return null;
    }

    public void deleteFolder(String folderName) throws Exception{
        try {
            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            Page<Blob> blobs =
                    storage.list(
                            bucketName,
                            Storage.BlobListOption.prefix("test"), // directoryPrefix is the sub directory.
                            Storage.BlobListOption.currentDirectory());

            for (Blob blob : blobs.iterateAll()) {
                blob.delete(Blob.BlobSourceOption.generationMatch());
            }
        }catch (Exception e)
        {
            log.error("An error occurred while uploading data. Exception: ", e);

        }
    }

}
