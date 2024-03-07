package com.walmart.aex.sp.service;

import com.walmart.aex.sp.exception.SizeAndPackException;

import java.util.List;
import java.util.Map;

public interface StoreClusterService {

    /**
     *
     * Fetch PO Store Grouping from Store Cluster API for the given Season & Fiscal Year
     *
     * @param season
     * @param fiscalYear
     * @return clusterName (like offshore/onshore) along with the list of store numbers
     * @throws SizeAndPackException
     */
    Map<String, List<Integer>> fetchPOStoreClusterGrouping(String season, String fiscalYear) throws SizeAndPackException;

}
