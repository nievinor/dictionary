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
        Map<String, Integer> result = search(check.toLowerCase(Locale.ROOT));
        model.addAttribute("dictionary", result);
        
        return "greeting";
    }

    private Map<String, Integer> search(String check) {
        //Map использую для хранения оценки для выражения и того, чтобы была связь между оценкой и выражение в словаре по индексу
        Map<Integer, Integer> markMap = new HashMap<>();

        //Инициализация словаря оценок нулевыми оценками
        for (int i = 0; i < dictionary.size(); i++) {
            markMap.put(i, 0);
        }

        //Проверяемое выражение переношу в массив, чтобы проверить на вхождение каждое слово в выражении. Используется ArrayList, так как get по индексу дает сложность O(1)
        List<String> checkArr = Arrays.asList(check.split(" "));

        //Цикл по выражениям словаря для анализа
        for (String str : dictionary) {

            //Разбиваю каждую строку из словаря для последующей проверки на вхождения слова в проверяемое выражение
            String[] arrayStrDictionary = str.split(" ");

            //считываем номер индекса для последующего выставления оценки
            int pos = dictionary.indexOf(str);

            //цикл для проверки вхождения 1 слова в словарь
            for (String s :arrayStrDictionary) {
                for (String checkStr : checkArr) {
                    if (s.equals(checkStr)) {
                        int mark = markMap.get(pos);
                        mark += 2;
                        markMap.put(pos, mark);
                    }
                }
            }

            //Копирую коллекцию провряемого выражения для проверки вхождения нескольких слов по порядку. Используется arrayList, т.к. get по индексу и удаление с конца коллекции имеет сложность О(1)
            List<String> copyArray = new ArrayList<>(checkArr);

            //цикл для проверки нескольких слов в словарь. Начинаю проверять все выражение и удаляю последнее слово, для сокращения вырадения проверки
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

        //накладываю отбор и сортирую map с оценками и связью с выражением
        markMap = markMap.entrySet().stream()
                .filter(mapEntry -> mapEntry.getValue() > 0)
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

//        создаю результирующую map где указано выражение и оценка для него
        Map<String, Integer> result = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry :
                markMap.entrySet()) {
            result.put(dictionary.get(entry.getKey()), entry.getValue());
        }
        return result;
    }

}
