package com.example.dictionary.controller;

import com.example.dictionary.service.Search;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
                dictionary.add(line.toLowerCase(Locale.ROOT));
            }
            model.addAttribute("message", "словарь загружен");
        } catch (IOException e) {
            model.addAttribute("message", "словарь не загружен");
        }

        if (dictionary.size() == 0) {
            model.addAttribute("message", "словарь не загружен");
            return "greeting";
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

        //Map использую для передачи результата, где ключ это выражение, а значение - оценка
        Map<String, Integer> result = Search.search(dictionary, check.toLowerCase(Locale.ROOT));
        model.addAttribute("dictionary", result);
        
        return "greeting";
    }
}
