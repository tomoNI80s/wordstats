# WordStats

Generates a statistics report on words contained in a text file.

## Description

WordsStats reads a text file breaks it up into an string array of words based on word boundaries and calculates several statistics, 
including the word count, the average length of a word, the frequency of the most common word length, and a table showing how often 
each word length appears in the text. It then shows all the statistics in a report output to the command line.

Word boundaries are defined by a regular expression so for example a text file containing `A man` will be split into a string array
with 2 words `A`, `man`. Another example would be `I have a surprise!` which would be split up int a string array with 4 words `I`
`have`, `a`, `surprise`. Please refer to the unit tests and source code for more examples.

## Getting Started

### Dependencies

* Git 2.34.1 or above
* JDK 17 or above

Note that Maven Wrapper is used so you do not need to have Maven installed locally.

### Git Cloning

This command will clone the project into a `base-dir` directory that you can choose. 

```
git clone https://github.com/tomoNI80s/wordstats.git
```
This command will cd you into `base-dir\wordstats` where you can
to test, build and run the program.

```
cd <base-dir>\wordstats
```
### Testing the Program

This command just executes the tests and displays only test failures in the console.

```
 ./mvnw -q clean test
```
### Building the Program

This command just builds, tests and packages WordStats into an executable jar in the target
folder.

```
./mvnw clean package
```
### Running the Program

Download any text file you want to generate word statistics for to a directory `absolute-path-to-text-file`. Make sure
the correct permissions to read the text file are sent and then run the following command from the root directory of 
the project.

```
java -jar target/wordstats-1.0-SNAPSHOT-jar-with-dependencies.jar <absolute-path-to-text-file>
```

A successful execution of the jar will produce a word statistics report output to the console similar to this
one but with different statistics.

```
Word count = 9
Average word length = 4.556
Number of words of length 1 is 1
Number of words of length 2 is 1
Number of words of length 3 is 1
Number of words of length 4 is 2
Number of words of length 5 is 2
Number of words of length 7 is 1
Number of words of length 10 is 1
The most frequently occurring word length is 2, for word lengths of 4 & 5
```
If you want to build and run the program from any other folder please consult Maven documentation on how to do this.

## Troubleshooting

* If you get a `NoSuchFileException` you spelt the name of your text file incorrectly.
* If you get a `IOException` with message `Is a directory` you didn't append the text file name to the end of the path.
* If you get a `NoSuchElementException` your text file is empty.

## Authors

Contributors names

ex. Tom Mooney 

## Version History

* 0.1
    * Initial Release