package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.List;
import java.util.Map;

public class HistoryTextHandler extends NgordnetQueryHandler {
    NGramMap ngm;
    public HistoryTextHandler(NGramMap ngm) {
        this.ngm = ngm;
    }

    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startyear = q.startYear();
        int endyear = q.endYear();
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            result.append(word).append(": {");

            TimeSeries tempword = ngm.weightHistory(word, startyear, endyear);
            if (tempword.isEmpty()) {
                result.append("}");
                result.append(System.lineSeparator());
                continue;
            }

            for (Map.Entry<Integer, Double> entry : tempword.entrySet()) {
                int year = entry.getKey();
                double data = entry.getValue();
                result.append(year).append("=").append(data).append(", ");

            }


            // 换行
            int length = result.length();
            result.delete(length - 2, length);
            result.append("}");
            result.append(System.lineSeparator());
        }

        return result.toString();

    }
}
