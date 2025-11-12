package com.techsol.tests.algorithms.thresholdfiltering;

import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class SequentialFilter implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Sequential Filter";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        long above = numbers.stream().filter(n -> n > 500_000).count();
        return Map.of("aboveThreshold", above);
    }
    
}
