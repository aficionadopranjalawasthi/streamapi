package com.pranjal.lambdastream2.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Assert;
import org.junit.Test;


public class StreamAPI {
    @Test
    public void testReductionOperation() {
        BigInteger result = LongStream.rangeClosed(1, 21)
                                      .mapToObj(BigInteger::valueOf)
                                      .reduce(BigInteger.ONE, BigInteger::multiply);
        System.out.println("result: " + result);
    }

    @Test
    public void functionCombination() {// IntUnaryOperator Interface(which is a Functional
                                       // Interface)
        List<IntUnaryOperator> operators = Arrays.asList(i -> i + 1, i -> i * 2, i -> i + 3);
        IntUnaryOperator combinedOperator = operators.stream()
                                                     .reduce(i -> i, IntUnaryOperator::andThen);
        Assert.assertEquals(15, combinedOperator.applyAsInt(5));

    }

    @Test
    public void testCollectorsToMap() {
        String[] versesInTrueSense = {"Your success and failure lure you and your opponent respectively",
                        "Your positivity is the ultimate source of energy for you", "Be a person of great integrity"};

        /*
         * Arrays.asList(versesInTrueSense) .stream() .collect(Collectors.toMap(line ->
         * line.substring(0, 1), line -> line));
         */

        Map<String, String> mapWithInvalidEntries1 = Arrays.asList(versesInTrueSense)
                                                           .stream()
                                                           .collect(
                                                               Collectors.toMap(line -> line.substring(0, 1), line -> line, (line1, line2) -> line1));
        System.out.println(mapWithInvalidEntries1);

        Map<String, String> mapWithInvalidEntries2 = Arrays.asList(versesInTrueSense)
                                                           .stream()
                                                           .collect(
                                                               Collectors.toMap(line -> line.substring(0, 1), line -> line, (line1, line2) -> line2));
        System.out.println(mapWithInvalidEntries2);

        Map<String, String> mapWithValidEntries = Arrays.asList(versesInTrueSense)
                                                        .stream()
                                                        .collect(Collectors.toMap(line -> line.substring(0, 1), line -> line,
                                                            (line1, line2) -> line1 + System.lineSeparator() + line2));
        System.out.println(mapWithValidEntries);
        System.out.println(mapWithValidEntries.size());
    }

    @Test
    public void testCollectorsGroupingBy() {
        String[] words = {"about", "this", "that", "those", "these", "apostrophe", "jamica"};
        Map<Integer, ArrayList<String>> wordsSortedOnLengthBasis_WithoutGrouppingByFunction = Arrays.asList(words)
                                                                                                    .stream()
                                                                                                    .collect(Collectors.toMap(String::length,
                                                                                                        s -> new ArrayList<>(Arrays.asList(s)),
                                                                                                        (a, b) -> {
                                                                                                            a.addAll(b);
                                                                                                            return a;
                                                                                                        }));
        System.out.println(wordsSortedOnLengthBasis_WithoutGrouppingByFunction);

        Map<Integer, List<String>> wordsSortedOnLengthBasis_WithGroupingByFunction = Arrays.asList(words)
                                                                                           .stream()
                                                                                           .collect(Collectors.groupingBy(String::length));
        System.out.println(wordsSortedOnLengthBasis_WithGroupingByFunction);
        Assert.assertTrue(wordsSortedOnLengthBasis_WithGroupingByFunction.equals(wordsSortedOnLengthBasis_WithoutGrouppingByFunction));

    }

    @Test
    public void testCollectorCounting() {// Counting is a Downstream Collector (stream().count)
        String[] words = {"about", "this", "that", "those", "these", "apostrophe", "jamica"};
        Map<String, Long> cascadingCounting = Arrays.asList(words)
                                                    .stream()
                                                    .collect(Collectors.groupingBy(line -> line.substring(0, 1), Collectors.counting()));
        System.out.println(cascadingCounting);
    }

    @Test
    public void testCollectorMapping() {// Mapping is a Downstream Collector (stream().map)
        String[] versesInTrueSense = {"Your success and failure lure you and your opponent respectively",
                        "Your positivity is the ultimate source of energy for you", "Be a person of great integrity"};
        Map<String, List<String>> groupedVerses = Arrays.asList(versesInTrueSense)
                                                        .stream()
                                                        .collect(Collectors.groupingBy(line -> line.substring(0, 1), Collectors.toList()));
        System.out.println(groupedVerses);

        Map<String, Set<String>> firstWordsOfGroupedVerses = Arrays.asList(versesInTrueSense)
                                                                   .stream()
                                                                   .collect(Collectors.groupingBy(line -> line.substring(0, 1),
                                                                       Collectors.mapping(line -> line.split(" +")[0], Collectors.toSet())));
        System.out.println(firstWordsOfGroupedVerses);
    }

    @Test
    public void calculateLetterFrequency() {
        String[] versesInTrueSense = {"Your success and failure lure you and your opponent respectively",
                        "Your positivity is the ultimate source of energy for you", "Be a person of great integrity"};
        Map<String, Long> frequencyOfLetters = Arrays.asList(versesInTrueSense)
                                                     .stream()// Stream of the lines of the verses
                                                     .flatMap(line -> expand(line).stream())// Stream
                                                     // of
                                                     // the
                                                     // letters
                                                     // of
                                                     // the
                                                     // verses
                                                     .collect(Collectors.groupingBy(letter -> letter, Collectors.counting()));
        frequencyOfLetters.forEach((letter, count) -> System.out.println(letter + "=> " + count));
    }

    @Test
    public void streamingMap() {
        Pattern pattern = Pattern.compile("\\s+");

        String[] versesInTrueSense = {"Your success and failure lure you and your opponents respectively and",
                        "Your positivity is the ultimate source of energy for you", "Be a person of great integrity is is "};

        Map<String, Long> words = Arrays.asList(versesInTrueSense)
                                        .stream()
                                        .map(String::toLowerCase)
                                        .flatMap(line -> pattern.splitAsStream(line))
                                        .collect(Collectors.collectingAndThen(Collectors.groupingBy(word -> word, Collectors.counting()),
                                            map -> copy(map)));

        //words.forEach((letter, count) -> System.out.println(letter + " => " + count));

        Map.Entry<String, Long> mostFrequentWord = words.entrySet()
                                                        .stream() // Stream(Map.Entry(String, Long))
                                                        .max(Map.Entry.comparingByValue())
                                                        .get();
        //System.out.println("Most frequent word is " + mostFrequentWord);
        // Map<Long, List<Map.Entry<String, Long>>>
        Map<Long, List<String>> otherWords = words.entrySet()// Stream<Map.Entry<String, Long>>
                                                  .stream()
                                                  .collect(Collectors.groupingBy(entry -> entry.getValue(),
                                                      Collectors.mapping(entry -> entry.getKey(), Collectors.toList())));

        Map.Entry<Long, List<String>> mostSeenWords = otherWords.entrySet()
                                                                .stream()
                                                                .max(Map.Entry.comparingByKey())
                                                                .get();
        System.out.println("Most seen words " + mostSeenWords);
    }

    private Map<String, Long> copy(Map<String, Long> map) {
        Map<String, Long> dummy = new HashMap<String, Long>();
        dummy.putAll(map);
        return dummy;
    }

    private List<String> expand(String s) {
        return s.codePoints()
                .mapToObj(codePoint -> Character.toString((char) codePoint))
                .collect(Collectors.toList());
    }
}
