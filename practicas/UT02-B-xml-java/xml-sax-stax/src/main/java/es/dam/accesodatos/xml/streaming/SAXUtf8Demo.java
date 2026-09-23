package es.dam.accesodatos.xml.streaming;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

public class SAXUtf8Demo {
    public static void main(String[] args) throws Exception {
        try (Reader reader = Files.newBufferedReader(
                Path.of("src/main/resources/file-utf.xml"), StandardCharsets.UTF_8)) {
            InputSource source = new InputSource(reader);
            source.setEncoding("UTF-8");
            SAXParserFactory.newInstance().newSAXParser().parse(source, new UserHandler());
        }
    }
}
