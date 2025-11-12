package com.techsol.tests.algorithms.evenodd;

import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class ModuloEvenOdd implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Modulo Check";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        long evens = numbers.stream().filter(n -> n % 2 == 0).count();
        System.out.println("Found " + evens + " even numbers");
        return Map.of("evens", evens, "odds", numbers.size() - evens);
    }

    
}
