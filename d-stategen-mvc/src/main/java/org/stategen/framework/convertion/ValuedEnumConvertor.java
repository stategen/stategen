/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.convertion;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.stategen.framework.util.EnumUtil;

/**
 * The Class ValuedEnumConvertor.
 */
@SuppressWarnings("rawtypes")
public class ValuedEnumConvertor implements ConverterFactory<String, Enum> {

    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToValuedEnum<T>(targetType);
    }

    /**
     * The Class StringToValuedEnum.
     *
     * @param <T> the generic type
     */
    private class StringToValuedEnum<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToValuedEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            return (T) EnumUtil.valueOf(enumType, source);
        }
    }

}