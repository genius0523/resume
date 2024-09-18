package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;
import org.knowm.xchart.XYChart;
import plotting.Plotter;

import java.util.ArrayList;
import java.util.List;

public class HistoryHandler extends NgordnetQueryHandler {
    NGramMap ngm;
    public HistoryHandler(NGramMap map) {
        this.ngm = map;
    }

    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startyear = q.startYear();
        int endyear = q.endYear();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<TimeSeries> lts = new ArrayList<>();
        for (String word:words) {
            lts.add(ngm.weightHistory(word, startyear, endyear));
            labels.add(word);
        }
        XYChart chart = Plotter.generateTimeSeriesChart(labels, lts);
        String encodedImage = Plotter.encodeChartAsString(chart);

        return encodedImage;
    }
}
