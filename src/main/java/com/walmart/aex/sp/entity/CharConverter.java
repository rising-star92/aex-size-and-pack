package com.walmart.aex.sp.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CharConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String value) {
        return value;
    }

    @Override
    public String convertToEntityAttribute(String value) {
        if (null == value) {
            return null;
        }
        return value.trim();
    }
}
