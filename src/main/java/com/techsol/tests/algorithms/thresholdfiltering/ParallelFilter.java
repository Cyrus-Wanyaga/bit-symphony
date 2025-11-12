package com.techsol.tests.algorithms.thresholdfiltering;

import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class ParallelFilter implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Parallel Stream Filter";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        long above = numbers.parallelStream().filter(n -> n > 500_000).count();
        return Map.of("aboveThreshold", above);
    }
    
}
