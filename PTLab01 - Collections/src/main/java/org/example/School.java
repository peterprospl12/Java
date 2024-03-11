package org.example;

import java.util.Map;

public class School {
    private final int sortingMethod;

    School(int sortingMethod){
        this.sortingMethod = sortingMethod;
        generateStudents();
    }

    void generateStudents(){
        Mage a = new Mage("Xardas", 11, 1.2, sortingMethod);
        Mage b = new Mage("Milten", 51, 11.2, sortingMethod);
        Mage c = new Mage("Mario", 19, 132.2, sortingMethod);
        Mage d = new Mage("Santiago", 27, 11.2, sortingMethod);
        Mage e = new Mage("Alan", 41, 12.2, sortingMethod);
        Mage f = new Mage("Diego", 32, 6.2, sortingMethod);
        Mage g = new Mage("Gabrio", 16, 3.2, sortingMethod);
        Mage h = new Mage("Nefarius", 16, 3.2, sortingMethod);
        Mage i = new Mage("Cronos", 16, 3.2, sortingMethod);
        Mage j = new Mage("Drago", 16, 3.2, sortingMethod);
        Mage k = new Mage("Saturas", 16, 3.2, sortingMethod);


        a.apprenticesInsert(c);
        a.apprenticesInsert(f);

        f.apprenticesInsert(k);

        c.apprenticesInsert(b);
        c.apprenticesInsert(g);

        b.apprenticesInsert(d);
        b.apprenticesInsert(e);
        b.apprenticesInsert(i);

        e.apprenticesInsert(h);

        h.apprenticesInsert(j);

        a.printApprentices();
        System.out.println("------------------------------");
        Map<Mage, Integer> map = a.apprenticesStats();

        for(Map.Entry<Mage, Integer> entry : map.entrySet()){
            System.out.println(entry.getKey() + " has {" + entry.getValue() + "} apprentices!");
        }

        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("------------------------------");
    }
}
