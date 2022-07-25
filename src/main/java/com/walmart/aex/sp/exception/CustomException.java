package com.walmart.aex.sp.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomException extends RuntimeException implements GraphQLError {

    public CustomException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> customAttributes = new LinkedHashMap<>();
        customAttributes.put("errorMessage", this.getMessage());
        return customAttributes;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return Collections.emptyList();
    }

    @Override
    public ErrorType getErrorType() {
        return null;
    }
}

