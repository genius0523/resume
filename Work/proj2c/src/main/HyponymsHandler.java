package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import browser.NgordnetQueryType;
import ngrams2b.NGramMap2b;


import java.util.List;

public class HyponymsHandler extends NgordnetQueryHandler {

    NGramMap2b ngm;
    public HyponymsHandler(
            String wordFile, String countFile,
            String synsetFile, String hyponymFile) {
        this.ngm = new NGramMap2b(wordFile, countFile, hyponymFile, synsetFile);
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();

        int k = q.k();
        int startyear = q.startYear();
        int endyear = q.endYear();
        NgordnetQueryType type = q.ngordnetQueryType();

        return ngm.multiwordResult(words, startyear, endyear, k, type);
    }


}
