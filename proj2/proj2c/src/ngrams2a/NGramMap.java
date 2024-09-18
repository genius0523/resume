package ngrams2a;

import edu.princeton.cs.algs4.In;

import java.util.*;

import static ngrams2a.TimeSeries.MAX_YEAR;
import static ngrams2a.TimeSeries.MIN_YEAR;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {
    class Tuple<B, C> {
        private final B year;
        private final C num;

        public Tuple(B year, C num) {
            this.year = year;
            this.num = num;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Tuple<?, ?> othertuple) {
                return othertuple.num.equals(this.num) && othertuple.year.equals(this.year);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(year, num);
        }
    }

    Map<Tuple<String, Integer>, Double> totalwordtimeseries;

    TimeSeries totalhistory;

    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        totalwordtimeseries = new HashMap<>();
        processfile(wordsFilename, countsFilename);
    }

    public void processfile(String wordsFilename, String countsFilename) {
        In wordfile = new In(wordsFilename);
        In countfile = new In(countsFilename);

        String tempword;
        int tempyear;
        double tempnum;

        while (wordfile.hasNextLine()) {
            String nextLine = wordfile.readLine();

            tempword = nextLine.split("\t")[0];
            tempyear = Integer.parseInt(nextLine.split("\t")[1].replace(",", ""));
            tempnum = Double.parseDouble(nextLine.split("\t")[2].replace(",", ""));

            totalwordtimeseries.put(new Tuple<>(tempword, tempyear), tempnum);
        }

        this.totalhistory = new TimeSeries();

        while (countfile.hasNextLine()) {
            String nextLine = countfile.readLine();

            tempyear = Integer.parseInt(nextLine.split(",")[0].replace(",", ""));
            tempnum = Double.parseDouble(nextLine.split(",")[1].replace(",", ""));

            this.totalhistory.put(tempyear, tempnum);
        }
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {

        TimeSeries wordhistory = new TimeSeries();

        for (int i = startYear; i <= endYear; i++) {
            if (totalwordtimeseries.get(new Tuple<>(word, i)) != null) {
                wordhistory.put(i, totalwordtimeseries.get(new Tuple<>(word, i)));
            }

        }

        return wordhistory;

    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        TimeSeries wordhistory = new TimeSeries();

        for (int i = MIN_YEAR; i <= MAX_YEAR; i++) {
            if (totalwordtimeseries.get(new Tuple<>(word, i)) != null) {
                wordhistory.put(i, totalwordtimeseries.get(new Tuple<>(word, i)));
            }

        }

        return wordhistory;
    }

    public TimeSeries totalCountHistory() {
        return totalhistory;
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */


    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        TimeSeries weighthistory;
        TimeSeries wordhistory = countHistory(word, startYear, endYear);
        if (wordhistory.isEmpty()) {
            return wordhistory;
        }
        weighthistory = wordhistory.dividedBy(totalhistory);
        return weighthistory;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        TimeSeries weighthistory;
        TimeSeries wordhistory = countHistory(word);
        if (wordhistory.isEmpty()) {
            return wordhistory;
        }
        weighthistory = wordhistory.dividedBy(totalhistory);
        return weighthistory;
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {
        TimeSeries summedweight = new TimeSeries();
        for (String word : words) {
            TimeSeries temp = weightHistory(word, startYear, endYear);
            summedweight = temp.plus(summedweight);
        }
        return summedweight;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries summedweight = new TimeSeries();
        for (String word : words) {
            TimeSeries temp = weightHistory(word);
            summedweight = temp.plus(summedweight);
        }
        return summedweight;
    }

    public Double countsum(String word, int startYear, int endYear) {

        Double summ = 0.0;

        for (int i = startYear; i <= endYear; i++) {
            if (totalwordtimeseries.get(new NGramMap.Tuple<>(word, i)) != null) {
                summ += totalwordtimeseries.get(new NGramMap.Tuple<>(word, i));
            }

        }

        return summ;

    }

    public int countsum(String word) {

        int summ = 0;

        for (int i = MIN_YEAR; i <= MAX_YEAR; i++) {
            if (totalwordtimeseries.get(new NGramMap.Tuple<>(word, i)) != null) {
                summ += totalwordtimeseries.get(new NGramMap.Tuple<>(word, i));
            }

        }

        return summ;

    }

}
