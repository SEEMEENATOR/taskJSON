package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

public class JsonSerializer {
    private final Set<Object> serializedObjects = new HashSet<>();

    public String serialize(Object obj) {
        return serialize(obj, 0);
    }

    private String serialize(Object obj, int indentLevel) {
        if (obj == null) {
            return "null";
        }

        serializedObjects.add(obj);
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        sb.append("{\n");

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                String fieldName = field.getName();
                Object fieldValue = field.get(obj);
                sb.append(getIndent(indentLevel + 1)).append("\"").append(fieldName).append("\": ");
                appendValue(sb, fieldValue, indentLevel);

                if (i < fields.length - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        sb.append(getIndent(indentLevel)).append("}");
        serializedObjects.remove(obj);
        return sb.toString();
    }

    private void appendValue(StringBuilder sb, Object value, int indentLevel) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append("\"").append(value).append("\"");
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof LocalDate) {
            sb.append("\"").append(value.toString()).append("\"");
        } else if (value instanceof UUID) {
            sb.append("\"").append(value.toString()).append("\"");
        } else if (value instanceof OffsetDateTime) {
            sb.append("\"").append(value.toString()).append("\"");
        } else if (value instanceof BigDecimal || value instanceof BigInteger) {
            sb.append(value.toString());
        } else if (value instanceof Enum<?>) {
            sb.append("\"").append(value.toString()).append("\"");
        } else if (value instanceof Map<?, ?>) {
            appendMap(sb, (Map<?, ?>) value, indentLevel);
        } else if (value instanceof Collection<?>) {
            appendCollection(sb, (Collection<?>) value, indentLevel);
        } else {
            sb.append(serialize(value, indentLevel));
        }
    }

    private void appendMap(StringBuilder sb, Map<?, ?> map, int indentLevel) {
        sb.append("{\n");
        int elementsCount = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append(getIndent(indentLevel + 1))
                    .append("\"").append(entry.getKey()).append("\": ");
            appendValue(sb, entry.getValue(), indentLevel + 1);

            if (elementsCount < map.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
            elementsCount++;
        }
        sb.append(getIndent(indentLevel)).append("}");
    }

    private void appendCollection(StringBuilder sb, Collection<?> collection, int indentLevel) {
        sb.append("[\n");
        int i = 0;
        for (Object element : collection) {
            appendValue(sb, element, indentLevel + 1);
            if (i < collection.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
            i++;
        }
        sb.append(getIndent(indentLevel)).append("]");
    }
    private String getIndent(int level) {
        return "  ".repeat(level);
    }
    public void writeToFile(Object obj, String filePath) {
        String jsonString = serialize(obj);
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON to file", e);
        }
    }
}
