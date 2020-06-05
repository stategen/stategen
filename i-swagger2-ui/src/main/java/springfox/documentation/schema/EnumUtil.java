package springfox.documentation.schema;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.ReflectionUtil;

import com.fasterxml.classmate.ResolvedType;

public class EnumUtil {
    public static String getDescIfIsEnum(ResolvedType resolvedType) {
        Class<?> erasedType = resolvedType.getErasedType();
        if (Enum.class.isAssignableFrom(erasedType)) {
            Field[] fields = erasedType.getDeclaredFields();
            List<Field> fieldList = new ArrayList<Field>();
            if (CollectionUtil.isNotEmpty(fields)) {
                for (Field field : fields) {
                    if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) {
                        continue;
                    }
                    Class<?> type = field.getType();
                    if (erasedType == type) {
                        continue;
                    }
                    if (type.isArray() && erasedType == type.getComponentType()) {
                        continue;
                    }
                    fieldList.add(field);
                }
            }

            Enum[] enums = (Enum[]) erasedType.getEnumConstants();
            if (CollectionUtil.isNotEmpty(enums)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < enums.length; i++) {
                    Enum enm = enums[i];
                    String name = enm.name();
                    sb.append(name);
                    if (CollectionUtil.isNotEmpty(fieldList)) {
                        sb.append('(');
                        for (int ii = 0; ii < fieldList.size(); ii++) {
                            Field field = fieldList.get(ii);
                            Object valueObj = ReflectionUtil.getFieldValue(enm, field);
                            if (valueObj != null) {
                                String value = valueObj.toString();
                                sb.append(value);
                            }
                            if (ii < fieldList.size() - 1) {
                                sb.append(',');
                            }
                        }
                        sb.append(")");
                    }
                    if (i < enums.length - 1) {
                        sb.append("; ");
                    } 
                }
                sb.append(".");
                return sb.toString();
            }
        }
        return null;
    }
}
