package com.techsol.tests.algorithms.primechecks;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class SievePrime implements ComputationalAlgorithm{

    @Override
    public String getName() {
        return "Sieve of Eratosthenes";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        int max = numbers.stream().max(Integer::compareTo).orElse(1);
        boolean[] prime = new boolean[max + 1];
        Arrays.fill(prime, true);
        prime[0] = prime[1] = false;
        for (int p = 2; p * p <= max; p++) {
            if (prime[p])
                for(int i = p * p; i <= max; i += p) {
                    prime[i] = false;
                }
        }

        long count = numbers.stream().filter(n -> n <= max && prime[n]).count();
        return Map.of("primesFound", count);
    }
    
}
