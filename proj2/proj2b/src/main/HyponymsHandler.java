package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;

import java.util.List;

public class HyponymsHandler extends NgordnetQueryHandler {

    NGramMap ngm;
    public HyponymsHandler(
            String wordFile, String countFile,
            String synsetFile, String hyponymFile) {
        this.ngm = new NGramMap(wordFile, countFile, hyponymFile, synsetFile);
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startyear = q.startYear();
        int endyear = q.endYear();

        return ngm.multiwordResult(words);
    }


}
