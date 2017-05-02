package service;

import org.h2.tools.Csv;


import java.io.*;
import java.sql.ResultSet;

public class ParseFileService {

    private final String[] fields = {"id", "productId", "userId", "profileName", "helpfulnessNumerator", "helpfulnessDenominator", "score",
            "time", "summary", "text"};

    public ResultSet getResultSetFromCsv(String pathToFile) {

        try {
            InputStream inputStream = new FileInputStream(new File(pathToFile));

            BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream, 4 * 1024)));

            Csv csvReader = new Csv();

            ResultSet rs = csvReader.read(reader, fields);

            return rs;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
