package com.techsol.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.techsol.config.Constants.WORDS_LIST_FILE_PATH;

public class WordListLoader {
    private final List<String> wordList;

    public WordListLoader() {
        this.wordList = new ArrayList<>();
        try {
            loadWordList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWordList() throws IOException {
        try {
            InputStream is = WordListLoader.class.getResourceAsStream(WORDS_LIST_FILE_PATH);

            System.out.println("Input stream is not null");
            if (is == null) {
                throw new IOException("Cannot find resource file: " + WORDS_LIST_FILE_PATH);
            }

            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            this.wordList.addAll(reader.lines().collect(Collectors.toList()));
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public List<String> getWordList() {
        return wordList;
    }

}
