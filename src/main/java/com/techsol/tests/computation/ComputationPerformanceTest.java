package com.techsol.tests.computation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.techsol.tests.PerformanceResult;
import com.techsol.tests.PerformanceTest;
import com.techsol.tests.TestResourceMonitor;
import com.techsol.tests.algorithms.ComputationalAlgorithm;
import com.techsol.tests.algorithms.aggregation.JavaStreamAggregate;
import com.techsol.tests.algorithms.aggregation.ManualAggregate;
import com.techsol.tests.algorithms.evenodd.BitwiseEvenOdd;
import com.techsol.tests.algorithms.evenodd.ModuloEvenOdd;
import com.techsol.tests.algorithms.primechecks.SievePrime;
import com.techsol.tests.algorithms.primechecks.TrialDivisionPrime;
import com.techsol.tests.algorithms.sorting.BubbleSortAlgorithm;
import com.techsol.tests.algorithms.sorting.CollectionsSortAlgorithm;
import com.techsol.tests.algorithms.sorting.QuickSortAlgorithm;
import com.techsol.tests.algorithms.thresholdfiltering.ParallelFilter;
import com.techsol.tests.algorithms.thresholdfiltering.SequentialFilter;

public class ComputationPerformanceTest implements PerformanceTest {
    public enum Mode {
        PRIME_CHECK, EVEN_ODD,
        GREATER_THAN, MAX_MIN_AVG, SORT
    }

    private final Path inputFile;
    private final Mode mode;
    private final int threshold;
    private final List<ComputationalAlgorithm> algorithms;

    public ComputationPerformanceTest(Path inputFile, Mode mode, int threshold) {
        this.inputFile = inputFile;
        this.mode = mode;
        this.threshold = threshold;
        this.algorithms = getAlgorithmsForMode(mode);
    }

    @Override
    public Map<String, Object> getConfiguration() {
        return Map.of(
                "inputFile", inputFile.toString(),
                "mode", mode.name(),
                "threshold", threshold);
    }

    @Override
    public String getName() {
        return "Computation Test (" + mode + ")";
    }

    @Override
    public PerformanceResult runTest(TestResourceMonitor monitor) throws Exception {
        monitor.start();
        List<Integer> numbers = loadNumbers(inputFile);

        Map<String, Object> allResults = new LinkedHashMap<>();

        for (ComputationalAlgorithm algo : algorithms) {
            System.gc();
            long start = System.currentTimeMillis();
            System.out.println("Start at :" + start);
            Map<String, Object> returnedMetrics = algo.run(numbers);
            long end = System.currentTimeMillis();
            System.out.println("End at :" + end);

            Map<String, Object> metrics = new LinkedHashMap<>(returnedMetrics);
            System.out.println("Algo name is: " + algo.getName());
            System.out.println("Metrics object so far: " + metrics.toString());
            metrics.put("algorithm", algo.getName());
            metrics.put("durationMillis", end - start);
            allResults.put(algo.getName(), metrics);
        }

        monitor.stop();
        allResults.putAll(monitor.getMetrics());

        return new PerformanceResult(getName(), 0, allResults);
    }

    private List<Integer> loadNumbers(Path inputFile) throws IOException {
        return Files.lines(inputFile).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private List<ComputationalAlgorithm> getAlgorithmsForMode(Mode mode) {
        return switch (mode) {
            case PRIME_CHECK -> List.of(new TrialDivisionPrime(), new SievePrime());
            case EVEN_ODD -> List.of(new ModuloEvenOdd(), new BitwiseEvenOdd());
            case GREATER_THAN -> List.of(new SequentialFilter(), new ParallelFilter());
            case MAX_MIN_AVG -> List.of(new JavaStreamAggregate(), new ManualAggregate());
            case SORT -> List.of(new BubbleSortAlgorithm(), new CollectionsSortAlgorithm(), new QuickSortAlgorithm());
        };
    }
}
