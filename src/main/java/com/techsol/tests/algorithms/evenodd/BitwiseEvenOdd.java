package com.techsol.tests.algorithms.evenodd;

import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class BitwiseEvenOdd implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Bitwise AND Check";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        long evens = numbers.stream().filter(n -> (n & 1) == 0).count();
        return Map.of("evens", evens, "odds", numbers.size() - evens);
    }
    
}
