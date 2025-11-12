package com.techsol.tests.algorithms.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.techsol.tests.algorithms.ComputationalAlgorithm;

public class QuickSortAlgorithm implements ComputationalAlgorithm {

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public Map<String, Object> run(List<Integer> numbers) {
        List<Integer> arr = new ArrayList<>(numbers);
        quickSort(arr, 0, arr.size() - 1);
        return Map.of("sorted", arr.size());
    }

    private void quickSort(List<Integer> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
    
    private int partition(List<Integer> arr, int low, int high) {
        int pivot = arr.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr.get(j) < pivot) {
                i++;
                Collections.swap(arr, i, j);
            }
        }
        Collections.swap(arr, i + 1, high);
        return i + 1;
    }
}
