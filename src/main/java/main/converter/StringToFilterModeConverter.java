package main.converter;


import main.service.strategy.enums.FilterMode;
import org.springframework.core.convert.converter.Converter;

public class StringToFilterModeConverter implements Converter<String, FilterMode> {
    @Override
    public FilterMode convert(String source) {
        return FilterMode.valueOf(source.toUpperCase());
    }
}
