package com.techsol.tests.algorithms.sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class BubbleSortAlgorithm implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        List<Integer> arr = new ArrayList<>(numbers);
        for (int i = 0; i < arr.size(); i++) {
            for (int j = 0; j < arr.size() - i - 1; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
        return Map.of("sorted", arr.size());
    }
    
}
