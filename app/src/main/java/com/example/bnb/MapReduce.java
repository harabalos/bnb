// src/main/java/com/example/bnb/MapReduce.java
package com.example.bnb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapReduce {

    public interface Mapper {
        void map(String key, Accommodation value, Context context);
    }

    public interface Reducer {
        void reduce(String key, List<Accommodation> values, Context context);
    }

    public static class Context {
        private Map<String, List<Accommodation>> intermediateData = new HashMap<>();

        public void write(String key, Accommodation value) {
            intermediateData.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        public Map<String, List<Accommodation>> getIntermediateData() {
            return intermediateData;
        }
    }

    public static Map<String, List<Accommodation>> execute(List<Accommodation> data, Mapper mapper, Reducer reducer) {
        // Step 1 map
        Context mapContext = new Context();
        for (Accommodation accommodation : data) {
            mapper.map(accommodation.getLocation(), accommodation, mapContext);
        }

        // Step 2 reduce
        Context reduceContext = new Context();
        for (Map.Entry<String, List<Accommodation>> entry : mapContext.getIntermediateData().entrySet()) {
            reducer.reduce(entry.getKey(), entry.getValue(), reduceContext);
        }

        return reduceContext.getIntermediateData();
    }
}
