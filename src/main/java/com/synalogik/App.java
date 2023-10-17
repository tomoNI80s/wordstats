package com.synalogik;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.text.StringSubstitutor;

public class App {
    private static final String WORD_BOUNDARY_REGEX = "[\\s+,\\*+.!,?,;,:]+";
    private static final int INITIAL_COUNT = 1;
    private static final int DECIMAL_PLACES = 3;

    static final String WORD_STAT_REPORT_TEMPLATE = """
        Word count = ${wordCount}
        Average word length = ${avgWordLen}
        ${wordLensToFreq}
        The most frequently occurring word length is ${maxFreq}, for word lengths of ${mostFreqWordLens}""";

    record WordStats (long wordCount, double avgWordLen, int maxFreq, SortedMap<Integer, Integer> wordLenToFreq) {}

    String loadTextFile(Path path) throws IOException {
        byte[] textFileBytes = Files.readAllBytes( path );
        return new String(textFileBytes);
    }

    String[] splitIntoWords(String contents) {
        return Stream.of(contents.split(WORD_BOUNDARY_REGEX))
        .filter(s -> !s.equals(""))
        .toArray(String[]::new);
    }

    WordStats calculateWordStats(String[] words) {
        if (words == null || words.length == 0){
            throw new IllegalArgumentException();
        }
        int[] wordLens  = Stream.of(words)
        .mapToInt(String::length)
        .toArray();

        SortedMap<Integer, Integer> wordLenToFreq = IntStream.of(wordLens).boxed()
        .collect(Collectors.toMap(Function.identity(), i -> INITIAL_COUNT, (i, j) -> i + 1, TreeMap::new));

        IntSummaryStatistics stats = IntStream.of(wordLens).summaryStatistics();
        double avgWordLen = Precision.round(stats.getAverage(), DECIMAL_PLACES);

        int maxFreq = Collections.max(wordLenToFreq.values());

        return new WordStats(stats.getCount(), avgWordLen, maxFreq, wordLenToFreq);
    }

    String createWordStatsReport(WordStats wordStats) {
        if (wordStats == null){
            throw new IllegalArgumentException();
        }

        String wordLensToFreq = wordStats.wordLenToFreq.entrySet().stream()
        .map(e -> String.format("Number of words of length %d is %d", e.getKey(), e.getValue()))
        .collect(Collectors.joining("\n"));

        String mostFreqWordLens = wordStats.wordLenToFreq.entrySet().stream()
        .filter(e -> e.getValue() == wordStats.maxFreq)
        .map(e -> String.valueOf(e.getKey()))
        .collect(Collectors.joining(" & "));

        Map<String, Object> params = Map.of(
            "wordCount",  wordStats.wordCount,
            "avgWordLen", wordStats.avgWordLen,
            "wordLensToFreq", wordLensToFreq,
            "maxFreq", wordStats.maxFreq,
            "mostFreqWordLens", mostFreqWordLens
        );

        return StringSubstitutor.replace(WORD_STAT_REPORT_TEMPLATE, params, "${", "}");
    }

    private void run(Path path) throws IOException{
        String contents = loadTextFile(path);
        String[] words = splitIntoWords(contents);
        WordStats wordStats = calculateWordStats(words);
        String wordStatsReport = createWordStatsReport(wordStats);
        System.out.println(wordStatsReport);
    }

    public static void main( String[] args ) throws IOException
    {
        Path path = Path.of(args[0]);
        new App().run(path);
    }
}
