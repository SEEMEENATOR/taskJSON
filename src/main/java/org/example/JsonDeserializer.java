package org.example;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

public class JsonDeserializer {
    private JsonParser parser = new JsonParser();

    public <T> T deserialize(String json, Class<T> clazz) {
        MyJsonObject jsonObject = parser.parseObject(json);
        return deserializeObject(jsonObject, clazz);
    }

    private <T> T deserializeObject(MyJsonObject jsonObject, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();

                if (jsonObject.getValues().containsKey(fieldName)) {
                    Object fieldValue = jsonObject.get(fieldName);
                    Object value = deserializeField(fieldValue, field.getType(), field);
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error during deserialization", e);
        }
    }

    private Object deserializeField(Object jsonValue, Class<?> fieldType, Field field) {
        if (jsonValue == null) {
            return null;
        }

        if (fieldType.equals(String.class)) {
            return jsonValue.toString();
        } else if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
            return parsePrimitiveOrWrapper(jsonValue, fieldType);
        } else if (fieldType.equals(LocalDate.class)) {
            return LocalDate.parse(jsonValue.toString());
        } else if (fieldType.equals(UUID.class)) {
            return UUID.fromString(jsonValue.toString());
        } else if (fieldType.equals(OffsetDateTime.class)) {
            return OffsetDateTime.parse(jsonValue.toString());
        } else if (Map.class.isAssignableFrom(fieldType)) {
            return deserializeMap((MyJsonObject) jsonValue, field);
        } else if (List.class.isAssignableFrom(fieldType)) {
            return deserializeList((MyJsonArray) jsonValue, field);
        } else {
            return deserializeObject((MyJsonObject) jsonValue, fieldType);
        }
    }

    private Object parsePrimitiveOrWrapper(Object jsonValue, Class<?> fieldType) {
        if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            return Integer.parseInt(jsonValue.toString());
        } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
            return Double.parseDouble(jsonValue.toString());
        } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
            return Long.parseLong(jsonValue.toString());
        } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
            return Float.parseFloat(jsonValue.toString());
        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            return Boolean.parseBoolean(jsonValue.toString());
        } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
            return Short.parseShort(jsonValue.toString());
        } else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
            return Byte.parseByte(jsonValue.toString());
        } else if (fieldType.equals(char.class) || fieldType.equals(Character.class)) {
            return jsonValue.toString().charAt(0);
        } else if (fieldType.equals(BigDecimal.class)) {
            return new BigDecimal(jsonValue.toString());
        } else if (fieldType.equals(BigInteger.class)) {
            return new BigInteger(jsonValue.toString());
        }
        throw new RuntimeException("Unsupported primitive type: " + fieldType.getName());
    }

    private Map<Object, Object> deserializeMap(MyJsonObject jsonObject, Field field) {
        try {
            Type genericType = field.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                throw new RuntimeException("Map fields must be parameterized");
            }

            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];

            Map<Object, Object> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : jsonObject.getValues().entrySet()) {
                Object key = parsePrimitiveOrWrapper(entry.getKey(), keyType);
                Object value = deserializeField(entry.getValue(), valueType, null);
                map.put(key, value);
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing Map", e);
        }
    }
    private List<Object> deserializeList(MyJsonArray jsonArray, Field field) {
        try {
            Type genericType = field.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                throw new RuntimeException("List fields must be parameterized");
            }

            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Class<?> elementType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

            List<Object> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                Object elementValue = jsonArray.get(i);
                Object value = deserializeField(elementValue, elementType, null);
                list.add(value);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing List", e);
        }
    }
}
