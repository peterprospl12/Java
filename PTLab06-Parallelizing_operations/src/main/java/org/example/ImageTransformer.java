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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ImageTransformer {
    private List<Path> files;
    private final List<Pair<String, BufferedImage>> images;
    private final ExecutorService executor;
    private AtomicInteger current_file;
    private final String input_path;
    private final String output_path;

    ImageTransformer(String input_path, String output_path, int threads_size) {
        this.input_path = input_path;
        this.output_path = output_path;
        this.files = new LinkedList<>();
        this.images = new LinkedList<>();
        this.executor = Executors.newFixedThreadPool(threads_size);
        this.current_file = new AtomicInteger(0);
    }

    public void run() {
        Path source = Path.of(input_path);
        try (Stream<Path> stream = Files.list(source)) {
            files = stream.toList();
        } catch (IOException e) {
            System.out.println("Error with getting images, probably wrong Path");
            throw new RuntimeException(e);
        }
        for (int i = 0; i < files.size(); i++) {
            executor.submit(this::transform);
        }
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }
    }

    private void transform() {
        Path file = getFile();
        if (file == null) {
            return;
        }
        BufferedImage image;
        try {
            image = ImageIO.read(file.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String name = String.valueOf(file.getFileName());
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                int red = color.getRed();
                int blue = color.getBlue();
                int green = 0;
                Color outColor = new Color(red, blue, green);
                copy.setRGB(i, j, outColor.getRGB());
            }
        }
        synchronized (images) {
            images.add(Pair.of(name, copy));
        }
        File outputFile = new File(output_path + name + ".png");
        try {
            ImageIO.write(copy, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized Path getFile() {
        int index = current_file.getAndIncrement();
        if (index < files.size()) {
            return files.get(index);
        } else {
            return null;
        }
    }

    public List<Pair<String, BufferedImage>> getImages() {
        return images;
    }
}
