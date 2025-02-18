package org.example.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CodeDumper {

    public static void main(String[] args) {
        // Укажите путь к директории с классами и файлами
        String sourceDir = "src/main"; // замените на путь к вашей директории с классами
        String outputFile = "output.txt"; // файл, в который будет записан код

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            File dir = new File(sourceDir);
            dumpFiles(dir, writer);
            System.out.println("Код успешно скопирован в " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dumpFiles(File dir, BufferedWriter writer) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    dumpFiles(file, writer); // Рекурсивно обходим подкаталоги
                } else if (isSupportedFile(file)) {
                    writeFileContent(file, writer);
                }
            }
        }
    }

    private static boolean isSupportedFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".java") || fileName.endsWith(".html") || fileName.endsWith(".css");
    }

    private static void writeFileContent(File file, BufferedWriter writer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            writer.write("// Код из файла: " + file.getAbsolutePath());
            writer.newLine();
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.newLine();
        }
    }
}
