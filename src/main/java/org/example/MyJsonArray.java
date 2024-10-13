package org.example;

import java.util.ArrayList;
import java.util.List;

public class MyJsonArray {
    private List<Object> elements = new ArrayList<>();

    public void add(Object value) {
        elements.add(value);
    }

    public Object get(int index) {
        return elements.get(index);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
