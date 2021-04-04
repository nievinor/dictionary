package com.example.dictionary.service;

import java.util.*;
import java.util.stream.Collectors;

public class Search {

    static public Map<String, Integer> search(List<String> dictionary, String check) {
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
