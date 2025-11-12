package com.techsol.tests.algorithms.aggregation;

import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class JavaStreamAggregate implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Stream Aggregate";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        int max = numbers.stream().max(Integer::compare).orElse(0);
        int min = numbers.stream().min(Integer::compare).orElse(0);
        double avg = numbers.stream().mapToInt(Integer::intValue).average().orElse(0);
        return Map.of("max", max, "min", min, "avg", avg);
    }
    
}
