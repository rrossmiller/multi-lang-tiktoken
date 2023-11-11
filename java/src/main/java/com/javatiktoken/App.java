package com.javatiktoken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;

public class App {
    public static void main(String[] args) throws Exception {
        int runs = Integer.parseInt(args[0]);
        boolean runTests;
        if (args.length > 1) {
            runTests = Boolean.parseBoolean(args[1]);
        } else {
            runTests = false;
        }

        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncodingForModel(ModelType.GPT_4);

        var tests = getTests();
        List<Long> times = new ArrayList<>();
        List<List<Integer>> allResults = new ArrayList<>();

        for (int i = 0; i < runs; i++) {
            List<Integer> results = new ArrayList<>();
            // var start = System.nanoTime();
            for (String t : tests) {
                var start = System.nanoTime();
                var r = enc.encode(t);
                var end = System.nanoTime() - start; // run time for individual sample
                times.add(end);
                results.add(r.size());
            }
            // var end = System.nanoTime() - start; // run time for whole suite
            // times.add(end);
            allResults.add(results);
        }

        double avgTime = times.stream().mapToLong(Long::longValue).sum() / times.size();
        // avgTime = (avgTime / 1e9);
        // System.out.println("Average time: " + String.format("%.9f", avgTime) +
        // "seconds");
        System.out.println("Average time: " + avgTime + " nano seconds");

        var allResultsMean = mean2d(allResults);

        // write the results to a csv
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Num Samples: %d\nAvg: %.9f\n", tests.size(), avgTime));
        for (int i = 0; i < allResultsMean.size(); i++) {
            if (i == allResultsMean.size() - 1) {
                sb.append(String.format("%d", allResultsMean.get(i)));
            } else {
                sb.append(String.format("%d,", allResultsMean.get(i)));
            }
        }
        sb.append("\n");
        for (int i = 0; i < times.size(); i++) {
            if (i == times.size() - 1) {
                sb.append(String.format("%d", times.get(i)));
            } else {
                sb.append(String.format("%d,", times.get(i)));
            }
        }

        try {
            var writer = new FileWriter("results.txt");
            writer.write(sb.toString());
            writer.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (runTests) {
            compareVsBaseline(allResultsMean);
        }
    }

    static List<String> getTests() {
        String file = "../input.txt";
        List<String> tests = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String contents = reader.lines().map(line -> line + "\n").reduce("", String::concat);
            var x = contents.split("\n\n");
            tests = List.of(x);
            reader.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        }

        System.out.println("Num Samples: " + tests.size());
        return tests;
    }

    static List<List<Integer>> transpose(List<List<Integer>> list) {
        int xl = list.get(0).size();
        int yl = list.size();
        var result = new ArrayList<List<Integer>>();

        for (int i = 0; i < xl; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < yl; j++) {
                row.add(list.get(j).get(i));
            }
            result.add(row);
        }

        return result;
    }

    static List<Integer> mean2d(List<List<Integer>> list) {
        var t = transpose(list);
        var result = new ArrayList<Integer>();
        for (List<Integer> l : t) {
            result.add(l.stream().mapToInt(Integer::intValue).sum() / l.size());
        }
        return result;
    }

    static void compareVsBaseline(List<Integer> results) throws IOException {
        String file = "../py/baseline.txt";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String contents = reader.lines().map(line -> line + "\n").reduce("", String::concat);
        var x = contents.split("\n");
        var baseline = List.of(x[2].split(","));
        reader.close();

        for (int i = 0; i < results.size(); i++) {
            assert (results.get(i) == Integer.parseInt(baseline.get(i)));
        }

        System.out.println("Results match baseline");
    }
}
