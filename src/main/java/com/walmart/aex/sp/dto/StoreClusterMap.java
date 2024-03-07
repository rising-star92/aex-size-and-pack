package com.walmart.aex.sp.dto;

import java.util.*;

public class StoreClusterMap extends HashMap<String, List<Integer>> {

    private final Map<Integer, String> reverseMap = new HashMap<>();

    @Override
    public List<Integer> put(String key, List<Integer> value) {

        Optional.ofNullable(value).stream()
                .flatMap(Collection::stream)
                .forEach(v -> reverseMap.put(v, key));

        return super.put(key, value);
    }

    public String getKey(Integer value){
        return reverseMap.get(value);
    }

}
