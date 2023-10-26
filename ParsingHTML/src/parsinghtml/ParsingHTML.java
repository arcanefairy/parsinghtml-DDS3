package parsinghtml;

import java.io.BufferedReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;




import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ParsingHTML {
    public static void main(String[] args) {
        try {
            String url = "https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html";
            Document doc = Jsoup.connect(url).get();

            Elements links = doc.select("a[href$=.csv]");
            List<Thread> threads = new ArrayList<>();

            for (Element link : links) {
                String csvUrl = link.attr("abs:href");
                String csvName = link.text();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL csvFile = new URL(csvUrl);
                            int lineCount = countLines(csvFile);
                            System.out.println("Nombre del archivo: " + csvName + ", Numero de lineas: " + lineCount);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

   private static int countLines(URL csvFile) throws IOException {
    int lineCount = 0;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.openStream()))) {
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
        for (CSVRecord record : parser) {
            lineCount++;
        }
    }
    return lineCount;
}
}