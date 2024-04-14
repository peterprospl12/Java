package org.example;

public class Main {
    public static void main(String[] args) {

        int min_threads = 2;
        int max_threads = 5;
        if(args.length == 1) {
            min_threads = Integer.parseInt(args[0]);
            max_threads = Integer.parseInt(args[0]);
        }
        String input = "/home/piotr/IdeaProjects/PTLab06-Parallelizing_operations/Images";
        String output = "/home/piotr/IdeaProjects/PTLab06-Parallelizing_operations/TransformedImages/";
        for (int i = min_threads; i <= max_threads; i++) {
            ImageTransformer2 imageTransformer2 = new ImageTransformer2(input, output , i);
            long time = System.currentTimeMillis();
            imageTransformer2.run();
            System.out.println("Threads: " + i + " | Time: " + ((System.currentTimeMillis() - time)) + "ms");
        }


    }
}


