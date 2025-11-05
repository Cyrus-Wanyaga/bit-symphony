package com.techsol.tests.computation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.techsol.tests.PerformanceResult;
import com.techsol.tests.PerformanceTest;
import com.techsol.tests.TestResourceMonitor;

public class ComputationPerformanceTest implements PerformanceTest {
    public enum Mode {
        PRIME_CHECK, EVEN_ODD, 
        //GREATER_THAN, MAX_MIN_AVG, SORT
    }

    private final Path inputFile;
    private final Mode mode;
    private final int threshold;

    public ComputationPerformanceTest(Path inputFile, Mode mode, int threshold) {
        this.inputFile = inputFile;
        this.mode = mode;
        this.threshold = threshold;
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
        long start = System.currentTimeMillis();

        List<Integer> numbers = Files.lines(inputFile)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Map<String, Object> metrics = switch (mode) {
            case PRIME_CHECK -> performPrimeCheck(numbers);
            case EVEN_ODD -> performEvenOdd(numbers);
        };

        long end = System.currentTimeMillis();
        monitor.stop();

        metrics.putAll(monitor.getMetrics());
        return new PerformanceResult(getName(), end - start, metrics);
    }

    private Map<String, Object> performPrimeCheck(List<Integer> nums) {
        long count = nums.stream().filter(this::isPrime).count();
        return Map.of("primesFound", count, "totalNumbers", nums.size());
    }

    private boolean isPrime(int n) {
        if (n <= 1)
            return false;
        if (n <= 3)
            return true;
        if (n % 2 == 0 || n % 3 == 0)
            return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        }

        return true;
    }

    private Map<String, Object> performEvenOdd(List<Integer> nums) {
        long evens = nums.stream().filter(n -> n % 2 == 0).count();
        long odds = nums.size() - evens;
        return Map.of("evens", evens, "odds", odds);
    }
}
