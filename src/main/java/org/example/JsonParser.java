package org.example;

public class JsonParser {
    private int index;
    private String json;

    public MyJsonObject parseObject(String json) {
        this.json = json.trim();
        this.index = 0;
        skipWhitespace();
        return parseJsonObject();
    }

    private MyJsonObject parseJsonObject() {
        MyJsonObject jsonObject = new MyJsonObject();
        consumeChar('{');
        skipWhitespace();

        while (peek() != '}') {
            String key = parseString();
            skipWhitespace();
            consumeChar(':');
            skipWhitespace();
            Object value = parseValue();
            jsonObject.put(key, value);
            skipWhitespace();

            if (peek() == ',') {
                consumeChar(',');
                skipWhitespace();
            }
        }

        consumeChar('}');
        return jsonObject;
    }

    private MyJsonArray parseJsonArray() {
        MyJsonArray jsonArray = new MyJsonArray();
        consumeChar('[');
        skipWhitespace();

        while (peek() != ']') {
            Object value = parseValue();
            jsonArray.add(value);
            skipWhitespace();

            if (peek() == ',') {
                consumeChar(',');
                skipWhitespace();
            }
        }

        consumeChar(']');
        return jsonArray;
    }

    private Object parseValue() {
        char c = peek();
        if (c == '"') {
            return parseString();
        } else if (c == '{') {
            return parseJsonObject();
        } else if (c == '[') {
            return parseJsonArray();
        } else if (Character.isDigit(c) || c == '-') {
            return parseNumber();
        } else if (json.startsWith("true", index)) {
            index += 4;
            return true;
        } else if (json.startsWith("false", index)) {
            index += 5;
            return false;
        } else if (json.startsWith("null", index)) {
            index += 4;
            return null;
        }

        throw new RuntimeException("Invalid JSON value at index: " + index);
    }

    private String parseString() {
        consumeChar('"');
        StringBuilder sb = new StringBuilder();

        while (peek() != '"') {
            sb.append(consumeChar());
        }

        consumeChar('"');
        return sb.toString();
    }

    private Number parseNumber() {
        int start = index;

        if (peek() == '-') {
            consumeChar();
        }

        while (Character.isDigit(peek())) {
            consumeChar();
        }

        if (peek() == '.') {
            consumeChar();
            while (Character.isDigit(peek())) {
                consumeChar();
            }
            return Double.parseDouble(json.substring(start, index));
        } else {
            return Integer.parseInt(json.substring(start, index));
        }
    }

    private char peek() {
        return json.charAt(index);
    }

    private char consumeChar() {
        return json.charAt(index++);
    }

    private void consumeChar(char expected) {
        if (json.charAt(index) != expected) {
            throw new RuntimeException("Expected '" + expected + "' at index " + index);
        }
        index++;
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(peek())) {
            index++;
        }
    }
}
