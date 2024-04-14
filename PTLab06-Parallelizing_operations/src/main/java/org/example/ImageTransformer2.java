package org.example;

import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ImageTransformer2 {
    private final String input_path;
    private final String output_path;
    private final int threads_size;
    private List<Path> files;

    ImageTransformer2(String input_path, String output_path, int threads_size) {
        this.input_path = input_path;
        this.output_path = output_path;
        this.files = new LinkedList<>();
        this.threads_size = threads_size;


    }

    public void run() {
        Path source = Path.of(input_path);
        try (Stream<Path> stream = Files.list(source)) {
            files = stream.toList();
        } catch (IOException e) {
            System.out.println("Error with getting images, probably wrong Path");
            throw new RuntimeException(e);
        }
        try {
            ForkJoinPool customThreadPool = new ForkJoinPool(this.threads_size);
            customThreadPool.submit(() -> files.parallelStream()
                    .map(this::makePair)
                    .map(this::transform)
                    .forEach(this::saveToFile));

            customThreadPool.shutdown();
            try {
                customThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }

    }

    void saveToFile(Pair<String, BufferedImage> pair) {
        File outputFile = new File(output_path + pair.getLeft());
        try {
            ImageIO.write(pair.getRight(), "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Pair<String, BufferedImage> makePair(Path file) {
        BufferedImage orginal_image;
        try {
            orginal_image = ImageIO.read(file.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String name = String.valueOf(file.getFileName());
        return Pair.of(name, orginal_image);
    }

    Pair<String, BufferedImage> transform(Pair<String, BufferedImage> pair) {
        String name = String.valueOf(pair.getLeft());
        BufferedImage orginal = pair.getRight();
        BufferedImage copy = new BufferedImage(orginal.getWidth(), orginal.getHeight(), orginal.getType());
        for (int i = 0; i < orginal.getWidth(); i++) {
            for (int j = 0; j < orginal.getHeight(); j++) {
                int rgb = orginal.getRGB(i, j);
                Color color = new Color(rgb);
                int red = color.getRed();
                int blue = color.getBlue();
                int green = 0;
                Color outColor = new Color(red, blue, green);
                copy.setRGB(i, j, outColor.getRGB());
            }
        }
        String new_name = "transformed_" + name + ".png";
        return Pair.of(new_name, copy);
    }

}
