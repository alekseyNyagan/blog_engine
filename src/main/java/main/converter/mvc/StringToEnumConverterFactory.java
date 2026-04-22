package main.converter.mvc;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    private record StringToEnumConverter<T extends Enum>(Class<T> enumType) implements Converter<String, T> {

        @Override
            public T convert(String source) {
                return (T) Enum.valueOf(this.enumType, source.toUpperCase());
            }
        }

    @Override
    public <T extends Enum> @NonNull Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return new StringToEnumConverter(targetType);
    }
}
