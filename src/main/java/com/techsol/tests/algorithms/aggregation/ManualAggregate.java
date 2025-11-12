package com.techsol.tests.algorithms.aggregation;

import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class ManualAggregate implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Manual Loop Aggregate";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        long sum = 0;
        for (int n : numbers) {
            if (n > max)
                max = n;
            if (n < min)
                min = n;
            sum += n;
        }

        double avg = (double) sum / numbers.size();
        return Map.of("max", max, "min", min, "avg", avg);
    }

}
