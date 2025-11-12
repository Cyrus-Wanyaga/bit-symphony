package com.techsol.tests.algorithms;

import java.util.List;
import java.util.Map;

public interface ComputationalAlgorithm {
    String getName();
    Map<String, Object> run(List<Integer> numbers);
}
