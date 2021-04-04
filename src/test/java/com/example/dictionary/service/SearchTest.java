package com.example.dictionary.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    final private List<String> dictionary = new ArrayList<>();

    @BeforeEach
    void setUp() throws FileNotFoundException {
        FileReader fr = new FileReader("1.txt");
        Scanner scan = new Scanner(fr);

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] strArr = line.split(" ");
            dictionary.add(line.toLowerCase(Locale.ROOT));
        }
    }

    @Test
    public void searchWithResult() {
        String check = "приходит во";
        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("аппетит приходит во время еды", 5);
        resultMap.put("приходит", 2);

        assertEquals(resultMap, Search.search(dictionary, check));
    }

    @Test
    public void searchEmpty() {
        String check = "автомобиль";
        Map<String, Integer> resultMap = new HashMap<>();

        assertEquals(resultMap, Search.search(dictionary, check));    }

}