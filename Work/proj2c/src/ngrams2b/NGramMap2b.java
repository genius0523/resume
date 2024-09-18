package ngrams2b;

import browser.NgordnetQueryType;
import edu.princeton.cs.algs4.In;

import java.util.*;


import static browser.NgordnetQueryType.HYPONYMS;


public class NGramMap2b {

    Map<Integer, List<Integer>> hyponyms;
    Map<Integer, Node> synsets;
    Map<String, List<Integer>> synsetsIndex;

    ngrams2a.NGramMap ngm2a;



    public class Node {
        List<Node> parent;
        List<Node> children;

        List<String> value;
        int index;
        public Node(int index, List<String> synsetsword) {
            this.index = index;
            this.value = synsetsword;
            this.children = new ArrayList<>();
            this.parent = new ArrayList<>();
        }

        public void connect(int iindex) {
            Node target = synsets.get(iindex);
            this.children.add(target);
            target.parent.add(this);
        }

    }

    public NGramMap2b(
            String wordFilename, String countFilename, String hyponymsFile, String synsetsFile
    ) {
        hyponyms = new HashMap<>();
        synsets = new HashMap<>();
        synsetsIndex = new HashMap<>();

        ngm2a = new ngrams2a.NGramMap(wordFilename, countFilename);

        dataReading(hyponymsFile, synsetsFile);
    }

    public void dataReading(String hyponymsFilename, String synsetsFilename) {
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

    public List<String> allancestors(Node target) {
        List<String> temp = new ArrayList<>();
        if (!target.parent.isEmpty()) {
            for (Node parent : target.parent) {
                temp.addAll(target.value);
                temp.addAll(allancestors(parent));
            }
        } else {
            temp.addAll(target.value);
        }

        return temp;

    }

    public List<String> onewordResult(String word, NgordnetQueryType type) {
        List<Node> allnodes = findwords(word);
        List<String> result = new ArrayList<>();

        if (type == HYPONYMS) {
            for (Node node : allnodes) {
                result.addAll(allhyponyms(node));
            }
        } else {
            for (Node node : allnodes) {
                result.addAll(allancestors(node));
            }
        }

        Set<String> uniqueSet = new HashSet<>(result);
        List<String> sortedList = new ArrayList<>(uniqueSet);
        Collections.sort(sortedList);

        return sortedList;
    }

    public class Item {
        String word;
        Double value;
        public Item(String word, Double num) {
            this.word = word;
            this.value = num;
        }

    }

    public class MyComparator implements Comparator<Item> {

        @Override
        public int compare(Item item1, Item item2) {

            return (int) (item1.value - item2.value);

        }
    }

    public String multiwordResult(List<String> words, int startyear, int endyear, int k, NgordnetQueryType type) {
        List<String> result = new ArrayList<>();

        int numm = 0;
        for (String word : words) {
            List<String> temp = onewordResult(word, type);
            if (result.isEmpty() && numm == 0) {
                result = temp;
            } else {
                result = result.stream().filter(temp::contains).distinct().toList();
            }
            numm += 1;
        }

        if (k == 0) {
            return "[" + String.join(", ", result) + "]";
        }

        Double num;

        PriorityQueue<Item> minHeap = new PriorityQueue<>(k, new MyComparator());
        for (String word : result) {
            num = ngm2a.countsum(word, startyear, endyear);
            Item temp = new Item(word, num);
            MyComparator tempcomp = new MyComparator();

            if (minHeap.size() < k && num != 0) {
                minHeap.add(temp);
            } else if (minHeap.size() >= k) {
                Item minItem = minHeap.peek();
                if (tempcomp.compare(temp, minItem) > 0) {
                    minHeap.poll();
                    minHeap.add(temp);
                }
            }
        }

        List<String> finalResult = new ArrayList<>();
        for (Item item : minHeap) {
            finalResult.add(item.word);
        }

        Collections.sort(finalResult);

        return "[" + String.join(", ", finalResult) + "]";
    }
}
















