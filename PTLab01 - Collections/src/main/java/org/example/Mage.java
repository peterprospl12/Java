package org.example;

import java.util.*;

public class Mage implements Comparator<Mage>, Comparable<Mage>{
    private final String name;
    private final int level;
    private final double power;
    private final int sortingMethod;
    private final Set<Mage> apprentices;

    Mage(String name, int level, double power, int sortingMethod) {
        // sortingMethod:
        // 0 = without sorting
        // 1 = natural sorting
        // 2 = alternative sorting
        this.name = name;
        this.level = level;
        this.power = power;
        this.sortingMethod = sortingMethod;

        switch (sortingMethod) {
            case 1 -> this.apprentices = new TreeSet<>();
            case 2 -> this.apprentices = new TreeSet<>(this);
            default -> this.apprentices = new HashSet<>();
        }
    }

    void apprenticesInsert(Mage a){
        this.apprentices.add(a);
    }

    void printApprentices() {
        Stack<Mage> mages = new Stack<>();
        Stack<Integer> recLevels = new Stack<>();
        Map<Mage, Integer> toPrint;

        switch(this.sortingMethod){
            case 1 -> toPrint = new TreeMap<>();
            case 2 -> toPrint = new TreeMap<>(this);
            default -> toPrint = new HashMap<>();
        }

        mages.push(this);
        recLevels.push(1);

        toPrint.put(this, 0);

        while(!mages.isEmpty()){
            Mage a1 = mages.peek();
            int recLevel = recLevels.peek();

            mages.pop();
            recLevels.pop();

            //System.out.println("Apprentices of: " + a1.name);
            for(Mage a2 : a1.apprentices){

                if(!a2.apprentices.isEmpty()){
                    mages.push(a2);
                    recLevels.push(recLevel+1);
                }

                /*
                for(int i=0;i<recLevel;i++){
                    System.out.print("-");
                }
                System.out.println(a2);
                */
                toPrint.put(a2, recLevel);
            }
        }

        for(Map.Entry<Mage, Integer> entry : toPrint.entrySet()){
            for(int i=0;i<entry.getValue();i++){
                System.out.print("-");
            }
            System.out.println(entry.getKey());
        }
    }

    Map<Mage, Integer> apprenticesStats() {
        Map<Mage, Integer> apprStats;

        // sortingMethod:
        // 0 = without sorting
        // 1 = natural sorting
        // 2 = alternative sorting
        switch(this.sortingMethod){
            case 1 -> apprStats = new TreeMap<>();
            case 2 -> apprStats = new TreeMap<>(this);
            default -> apprStats = new HashMap<>();
        }

        apprenticesFill(apprStats);

        return apprStats;
    }

    private int apprenticesFill(Map<Mage, Integer> apprStats){
        int counter = this.apprentices.size();
        if(counter == 0){
            apprStats.put(this, 0);
        }
        else{
            for(Mage a : this.apprentices){
                counter += a.apprenticesFill(apprStats);
            }
            apprStats.put(this, counter);
        }
        return counter;
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, level, power);
    }

    @Override
    public int compare(Mage o1, Mage o2) {
        int levelComp = Integer.compare(o1.level, o2.level);
        if(levelComp == 0){
            int nameComp = o1.name.compareTo(o2.name);
            if(nameComp == 0){
                return Double.compare(o1.power, o2.power);
            }
            else{
                return nameComp;
            }
        }
        else {
            return levelComp;
        }
    }

    @Override
    public boolean equals(Object a){
        if(a == this) {
            return true;
        }
        if(a == null || getClass() != a.getClass()){
            return false;
        }

        Mage mage = (Mage) a;
        return Objects.equals(this.name, mage.name)
                && this.level == mage.level
                && Double.compare(this.power, mage.power) == 0;
    }

    @Override
    public String toString(){
        return String.format("Mage{name='%s', level=%d, power=%f}", this.name, this.level, this.power);
    }

    @Override
    public int compareTo(Mage a) {
        int nameComp = this.name.compareTo(a.name);
        if(nameComp == 0){
            int levelComp = Integer.compare(this.level, a.level);
            if(levelComp == 0){
                return Double.compare(this.power, a.power);
            }
            else{
                return levelComp;
            }
        }
        else {
            return nameComp;
        }

    }
}