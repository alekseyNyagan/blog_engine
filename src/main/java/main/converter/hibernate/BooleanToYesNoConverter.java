package main.converter.hibernate;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanToYesNoConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean aBoolean) {
        return Boolean.TRUE.equals(aBoolean) ? "YES" : "NO";
    }

    @Override
    public Boolean convertToEntityAttribute(String s) {
        return s.equals("YES") ? Boolean.TRUE : Boolean.FALSE;
    }
}
