package com.javatiktoken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;

public class App {
    public static void main(String[] args) {
        int runs = Integer.parseInt(args[0]);

        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncodingForModel(ModelType.GPT_4);

        var test = getTests();
        List<Long> times = new ArrayList<>();
        List<List<Integer>> allResults = new ArrayList<>();

        for (int i = 0; i < runs; i++) {
            List<Integer> results = new ArrayList<>();
            var start = System.nanoTime();
            for (String t : test) {
                var r = enc.encode(t);
                results.add(r.size());
            }
            var end = System.nanoTime() - start;
            times.add(end);
            allResults.add(results);
        }

        double avgTime = times.stream().mapToLong(Long::longValue).sum() / times.size();
        avgTime = (avgTime / 1e9);
        System.out.println("Average time: " + String.format("%.9f", avgTime) + "seconds");

        var allResultsMean = mean2d(allResults);

        // write the results to a csv
        try {
            var writer = new FileWriter("../results.txt");
            for (int i = 0; i < allResultsMean.size(); i++) {
                writer.write(allResultsMean.get(i) + "," + times.get(i) + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static List<String> getTests() {
        String file = "../input.txt";
        List<String> tests = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            for (String line : reader.lines().toList()) {
                if (line.length() == 0) {
                    tests.add(sb.toString());
                    sb = new StringBuilder();
                    continue;
                }
                sb.append(line + "\n");
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        }

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
}
