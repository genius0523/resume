package ngrams;

import edu.princeton.cs.algs4.In;

import java.util.*;

public class NGramMap {

    Map<Integer, List<Integer>> hyponyms;
    Map<Integer, Node> synsets;
    Map<String, List<Integer>> synsetsIndex;



    public class Node {
        Node parent;
        List<Node> children;

        List<String> value;
        int index;
        public Node(int index, List<String> synsetsword) {
            this.index = index;
            this.value = synsetsword;
            this.children = new ArrayList<>();
        }

        public void connect(int iindex) {
            Node target = synsets.get(iindex);
            this.children.add(target);
            target.parent = this;
        }

    }

    public NGramMap(String wordFile, String countFile, String hyponymsFile, String synsetsFile) {
        hyponyms = new HashMap<>();
        synsets = new HashMap<>();
        synsetsIndex = new HashMap<>();

        dataReading(wordFile, countFile, hyponymsFile, synsetsFile);
    }

    public void dataReading(
            String wordFilename, String countFilename, String hyponymsFilename, String synsetsFilename
    ) {
        In hyponymsfile = new In(hyponymsFilename);
        In synsetsfile = new In(synsetsFilename);

        int index;

        while (synsetsfile.hasNextLine()) {

            String nextLine = synsetsfile.readLine();
            String[] allwords = nextLine.split(",");
            String[] splitwords = allwords[1].split(" ");

            index = Integer.parseInt(allwords[0]);
            List<String> templist = new ArrayList<>(Arrays.asList(splitwords));

            for (String word : splitwords) {
                if (synsetsIndex.containsKey(word)) {
                    synsetsIndex.get(word).add(index);
                } else {
                    List<Integer> transfer = new ArrayList<>();
                    transfer.add(index);
                    synsetsIndex.put(word, transfer);
                }
            }

            synsets.put(index, new Node(index, templist));
        }

        while (hyponymsfile.hasNextLine()) {
            String nextLine = hyponymsfile.readLine();
            String[] temp = nextLine.split(",");

            index = Integer.parseInt(temp[0]);
            Node point = synsets.get(index);

            for (int i = 1; i < temp.length; i++) {
                point.connect(Integer.parseInt(temp[i]));
            }
        }

    }




    public List<Node> findwords(String word) {
        List<Node> tempnodes = new ArrayList<>();
        if (synsetsIndex.get(word) != null) {
            List<Integer> tempindexs = synsetsIndex.get(word);
            for (int i : tempindexs) {
                tempnodes.add(synsets.get(i));
            }
        }
        return tempnodes;
    }

    public List<String> allhyponyms(Node target) {
        List<String> temp = new ArrayList<>();

        if (!target.children.isEmpty()) {
            for (Node child : target.children) {
                temp.addAll(target.value);
                temp.addAll(allhyponyms(child));
            }
        } else {
            temp.addAll(target.value);
        }

        return temp;
    }

    public List<String> onewordResult(String word) {
        List<Node> allnodes = findwords(word);
        List<String> result = new ArrayList<>();
        for (Node node : allnodes) {
            result.addAll(allhyponyms(node));
        }

        Set<String> uniqueSet = new HashSet<>(result);
        List<String> sortedList = new ArrayList<>(uniqueSet);
        Collections.sort(sortedList);

        return sortedList;
    }

    public String multiwordResult(List<String> words) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            List<String> temp = onewordResult(word);
            if (result.isEmpty()) {
                result = temp;
            } else {
                result = result.stream().filter(temp::contains).distinct().toList();
            }
        }
        return "[" + String.join(", ", result) + "]";
    }
}
















