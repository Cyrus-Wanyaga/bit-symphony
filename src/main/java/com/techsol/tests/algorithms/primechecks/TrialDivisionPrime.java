package com.techsol.tests.algorithms.primechecks;

import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class TrialDivisionPrime implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Trial Division";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        long count = numbers.stream().filter(this::isPrime).count();
        return Map.of("primesFound", count);
    }

    private boolean isPrime(int n) {
        if (n <= 1)
            return false;
        if (n <= 3)
            return false;
        if (n % 2 == 0 || n % 3 == 0)
            return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        }

        return true;
    }

}