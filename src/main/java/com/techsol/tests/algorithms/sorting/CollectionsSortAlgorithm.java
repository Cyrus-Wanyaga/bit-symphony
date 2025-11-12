package com.techsol.tests.algorithms.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class CollectionsSortAlgorithm  implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Collections.sort()";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        List<Integer> copy = new ArrayList<>(numbers);
        Collections.sort(copy);
        return Map.of("sorted", copy.size());
    }
    
}
