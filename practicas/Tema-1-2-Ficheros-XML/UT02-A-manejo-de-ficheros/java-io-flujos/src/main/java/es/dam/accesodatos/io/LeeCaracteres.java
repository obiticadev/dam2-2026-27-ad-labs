package es.dam.accesodatos.io;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LeeCaracteres {
    public static void main(String[] args) {
        try (FileReader reader = new FileReader("src/main/resources/HolaMundo.txt", StandardCharsets.UTF_8)) {
            int caracter;
            while ((caracter = reader.read()) != -1) {
                System.out.println((char) caracter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
