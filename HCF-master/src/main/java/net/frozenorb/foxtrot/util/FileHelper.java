package net.frozenorb.foxtrot.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by vape on 10/30/2020 at 3:28 PM.
 */
@UtilityClass
public class FileHelper {

    public String readFile(File file) {
        StringBuilder builder = new StringBuilder();

        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line).append("\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return builder.toString();
    }

    public void writeFile(File file,String content) {
        try {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}