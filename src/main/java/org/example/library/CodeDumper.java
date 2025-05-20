package org.example.library;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodeDumper {

    private static final Logger LOGGER = Logger.getLogger(CodeDumper.class.getName());
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".java", ".html", ".css");

    public static void main(String[] args) {
        // Укажите путь к директории с классами и файлами
        String sourceDir = "src/main"; // замените на путь к вашей директории с классами
        String outputFile = "output.txt"; // файл, в который будет записан код

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            File dir = new File(sourceDir);
            dumpFiles(dir, writer);
            System.out.println("Код успешно скопирован в " + outputFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка при копировании кода", e);
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
        return SUPPORTED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
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

