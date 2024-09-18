package core;

import java.util.ArrayList;
import java.util.HashMap;

public class DisjointSet<T> {
    //implements disjoint set w/ generic arraylist passed in
    private ArrayList<SetObjects> myElems;
    private HashMap<T, Integer> myTracker; //keeps track of which rooms map to which indices

    public DisjointSet(ArrayList<T> elems) {
        myElems = new ArrayList<>();
        myTracker = new HashMap<>();
        for (int i = 0; i < elems.size(); i++) {
            SetObjects myObj = new SetObjects((World.Room) elems.get(i), -1);
            myElems.add(myObj);
            myTracker.put(elems.get(i), i);
        }
    }

    public void connect(T itemA, T itemB) {
        //get indices of parent of each item
        int parentA = findParent(itemA);
        int parentB = findParent(itemB);

        //merge the two, smaller joins larger
        int numChildrenA = -1 * myElems.get(parentA).numChildren;
        int numChildrenB = -1 * myElems.get(parentB).numChildren;
        if (numChildrenA <= numChildrenB) {
            myElems.get(parentB).numChildren = -1 * (numChildrenA + numChildrenB);
            myElems.get(parentA).numChildren = parentB;
        } else {
            myElems.get(parentA).numChildren = -1 * (numChildrenA + numChildrenB);
            myElems.get(parentB).numChildren = parentA;
        }
    }

    public boolean isConnected(T itemA, T itemB) {
        boolean truthVal = (findParent(itemA) == findParent(itemB));
        return truthVal;
    }

    //find index of parent of item
    public int findParent(T item) {
        // get index of item from hashmap
        int index = myTracker.get(item);

        SetObjects parent = myElems.get(index);
        while (parent.numChildren >= 0) {
            parent = myElems.get(parent.numChildren);
        }
        return myElems.indexOf(parent);
    }

    public class SetObjects {
        private World.Room room;
        private Integer numChildren;

        public SetObjects(World.Room rm, int num) {
            room = rm;
            numChildren = num;
        }

    }



}
