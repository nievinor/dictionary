package com.example.dictionary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("dictionary_search")
public class DictionaryController {

    final private List<String> dictionary = new ArrayList<>();

    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

    @PostMapping("dictionary_upload")
    public String readFile(Model model, @RequestParam("file") MultipartFile file) {
        BufferedReader br;
        dictionary.clear();

        try {
            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                dictionary.add(line);
            }
            model.addAttribute("message", "словарь загружен");
        } catch (IOException e) {
            model.addAttribute("message", "словарь не загружен");
        }
        return "greeting";
    }

    @PostMapping("/dictionary_search")
    public String searchController(Model model, @RequestParam("search") String check) {

        if (dictionary.size() == 0) {
            model.addAttribute("message", "словарь не загружен");
            return "greeting";
        } else {
            model.addAttribute("message", "словарь загружен");
        }

        Map<Integer, Integer> markMap = new HashMap<>();

        for (int i = 0; i < dictionary.size(); i++) {
            markMap.put(i, 0);
        }

        List<String> checkArr = Arrays.asList(check.split(" "));

        for (String str : dictionary) {
            String[] arrayStrDictionary = str.split(" ");
            List<String> copyArray = new ArrayList<>(checkArr);

            int pos = dictionary.indexOf(str);

            for (String s :arrayStrDictionary) {
                for (String checkStr : checkArr) {
                    if (s.equals(checkStr)) {
//                        int pos = dictionary.indexOf(str);
                        int mark = markMap.get(pos);
                        mark += 2;
                        markMap.put(pos, mark);
                    }
                }
            }

            for (int i = copyArray.size(); i >= 0 ; i--) {
                String checkStr = String.join(" ", copyArray);
                if (str.contains(checkStr)) {
                    int mark = markMap.get(pos);
                    mark += (copyArray).size() - 1;
                    markMap.put(pos, mark);
                    break;
                } else {
                    copyArray.remove(copyArray.size() - 1);
                    if (copyArray.size() == 1) break;
                }

            }

        }

        Map<Integer, Integer> collect = markMap.entrySet().stream()
                .filter(mapEntry -> mapEntry.getValue() > 0)
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Integer> result = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry :
                collect.entrySet()) {
            result.put(dictionary.get(entry.getKey()), entry.getValue());
        }

        model.addAttribute("dictionary", result);
        
        return "greeting";
    }

//    private Map<String, Integer> search() {
//
//    }

}
