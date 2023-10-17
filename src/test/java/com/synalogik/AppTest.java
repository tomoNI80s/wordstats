package com.synalogik;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.synalogik.App.WordStats;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class AppTest {
    private App sut;

    @BeforeEach
    public void setUp(){
        sut = new App();
    }

    @Test void shouldLoadTextFile() throws IOException{
        // Given
        FileSystem fs = Jimfs.newFileSystem( Configuration.forCurrentPlatform() );

        Path tempDir = fs.getPath( "temp" );
        Files.createDirectory( tempDir );
        Path path = tempDir.resolve( "myfile.txt" );
        String contents = "Hello world & good morning. The date is 18/05/2016";
        Files.write( path, contents.getBytes() );

        String expected = contents;

        // // When
        String actual = sut.loadTextFile(path);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> splitIntoWordsExamples(){
        return Stream.of(
            Arguments.of(
                """
                Hello world & good morning. The date is 18/05/2016
                """,
                new String[]{"Hello", "world", "&", "good", "morning", "The" ,"date", "is", "18/05/2016"}
            ),
            Arguments.of(
                """
                Normal activities took extraordinary amounts of concentration at the 
                high altitude. It doesn't sound like that will ever be on my travel 
                list.
                """,
                new String[]{"Normal", "activities", "took", "extraordinary", "amounts", "of" ,"concentration", "at", "the",
                "high", "altitude", "It", "doesn't", "sound", "like" ,"that", "will", "ever", "be", "on", "my", "travel", "list"}
            ),
            Arguments.of(
                """
                You’re not allowed to use your phone, camera or computer here.

                I need to start stealing things.

                Why  not?   
                """,
                new String[]{"You’re", "not", "allowed", "to", "use", "your" ,"phone", "camera", "or",
                "computer", "here", "I", "need", "to", "start" ,"stealing", "things", "Why", "not"}
            ),
            Arguments.of(
                """

                Complaining is a bad bad habit.

                They met Pablo Neruda before he was famous.

                """,
                new String[]{"Complaining", "is", "a", "bad", "bad", "habit" ,"They", "met", "Pablo",
                "Neruda", "before", "he", "was", "famous"}
            ),
            Arguments.of(
                """

                ******* December 25 *******

                Woke up early.

                Opened all my presents.

                """,
                new String[]{"December", "25", "Woke", "up", "early", "Opened" ,"all", "my", "presents"}
            ),
            Arguments.of(
                """
                I like to listen to music; I prefer jazz.
                """,
                new String[]{"I", "like", "to", "listen", "to", "music" ,"I", "prefer", "jazz"}
            ),
            Arguments.of(
                """
                Yay, I’m excited: I bounce on my heels and check my phone.
                """,
                new String[]{"Yay", "I’m", "excited", "I", "bounce", "on" ,"my", "heels", "and", "check", "my", "phone"}
            )
        );
    }

    @MethodSource("splitIntoWordsExamples")
    @ParameterizedTest void shouldSplitContentsIntoWords(String contents, String[] expected){
        // When
        String[] actual = sut.splitIntoWords(contents);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> calculateWordStatsExamples(){
        return Stream.of(
            Arguments.of(
                new String[]{"Hello", "world", "&", "good", "morning", "The" ,"date", "is", "18/05/2016"},
                new WordStats(9, 4.556, 2, new TreeMap<>(Map.of(1, 1, 2, 1, 3, 1, 4, 2, 5, 2, 7, 1, 10, 1)))
            ),
            Arguments.of(
                new String[]{"Normal", "activities", "took", "extraordinary", "amounts", "of" ,"concentration", "at", "the",
                "high", "altitude", "It", "doesn't", "sound", "like" ,"that", "will", "ever", "be", "on", "my", "travel", "list"},
                new WordStats(23, 5.130, 7, new TreeMap<>(Map.of(2, 6, 3, 1, 4, 7, 5, 1, 6, 2, 7, 2, 8, 1, 10, 1, 13, 2)))
            ),
            Arguments.of(
                new String[]{"You’re", "not", "allowed", "to", "use", "your" ,"phone", "camera", "or",
                "computer", "here", "I", "need", "to", "start" ,"stealing", "things", "Why", "not"},
                new WordStats(19, 4.316, 4, new TreeMap<>(Map.of(1, 1, 2, 3, 3, 4, 4, 3, 5, 2, 6, 3, 7, 1, 8, 2)))
            ),
            Arguments.of(
                new String[]{"Complaining", "is", "a", "bad", "bad", "habit" ,"They", "met", "Pablo",
                "Neruda", "before", "he", "was", "famous"},
                new WordStats(14, 4.286, 4, new TreeMap<>(Map.of(1, 1, 2, 2, 3, 4, 4, 1, 5, 2, 6, 3, 11, 1)))
            ),
            Arguments.of(
                new String[]{"December", "25", "Woke", "up", "early", "Opened" ,"all", "my", "presents"},
                new WordStats(9, 4.444, 3, new TreeMap<>(Map.of(2, 3, 3, 1, 4, 1, 5, 1, 6, 1, 8, 2)))
            ),
            Arguments.of(
                new String[]{"I", "like", "to", "listen", "to", "music" ,"I", "prefer", "jazz"},
                new WordStats(9, 3.444, 2, new TreeMap<>(Map.of(1, 2, 2, 2, 4, 2, 5, 1, 6, 2)))
            ),
            Arguments.of(
                new String[]{"Yay", "I’m", "excited", "I", "bounce", "on" ,"my", "heels", "and", "check", "my", "phone"},
                new WordStats(12, 3.667, 3, new TreeMap<>(Map.of(1, 1, 2, 3, 3, 3, 5, 3, 6, 1, 7, 1)))
            )
        );
    }

    @MethodSource("calculateWordStatsExamples")
    @ParameterizedTest void shouldCalculateWordStatsCorrectly(String[] words, WordStats expected){
        // When
        WordStats actual = sut.calculateWordStats(words);

        // Then
        assertThat(actual).usingRecursiveComparison()
                    .withStrictTypeChecking()
                    .isEqualTo(expected);
    }

    @Test void shouldThrowAnIllegalArgumentExceptionIfCalculateWordStatsRecievesAnEmptyWordsArray(){
        // Given
        String[] emptyWords = new String[]{};

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> sut.calculateWordStats(emptyWords) );
    }

    @Test void shouldThrowAnIllegalArgumentExceptionIfCalculateWordStatsRecievesAnNullWordsArray(){
        // Given
        String[] nullWords = null;

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> sut.calculateWordStats(nullWords) );
    }

    static Stream<Arguments> createWordStatsReportExamples(){
        return Stream.of(
            Arguments.of(
                new WordStats(9, 4.556, 2, new TreeMap<>(Map.of(1, 1, 2, 1, 3, 1, 4, 2, 5, 2, 7, 1, 10, 1))),
                """
                Word count = 9
                Average word length = 4.556
                Number of words of length 1 is 1
                Number of words of length 2 is 1
                Number of words of length 3 is 1
                Number of words of length 4 is 2
                Number of words of length 5 is 2
                Number of words of length 7 is 1
                Number of words of length 10 is 1
                The most frequently occurring word length is 2, for word lengths of 4 & 5"""

            ),
            Arguments.of(
                new WordStats(12, 3.667, 3, new TreeMap<>(Map.of(1, 1, 2, 3, 3, 3, 5, 3, 6, 1, 7, 1))),
                """
                Word count = 12
                Average word length = 3.667
                Number of words of length 1 is 1
                Number of words of length 2 is 3
                Number of words of length 3 is 3
                Number of words of length 5 is 3
                Number of words of length 6 is 1
                Number of words of length 7 is 1
                The most frequently occurring word length is 3, for word lengths of 2 & 3 & 5"""
            )
        );
    }
    @MethodSource("createWordStatsReportExamples")
    @ParameterizedTest void shouldCreateWordStatsReportCorrectly(WordStats wordStats, String expected){
    
        // When
        String actual = sut.createWordStatsReport(wordStats);

        // Then
        assertThat(actual).isEqualTo(expected);
    }


    @Test void shouldThrowAnIllegalArgumentExceptionIfCreateWordStatsReportRecievesNullWordStats(){
        // Given
        WordStats nullWordStats = null;

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> sut.createWordStatsReport(nullWordStats) );
    }

}
