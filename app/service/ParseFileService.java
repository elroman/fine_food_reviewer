package service;

import actors.WorkerSupervisorActor;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.h2.tools.Csv;
import scala.Tuple2;

import java.io.*;
import java.sql.ResultSet;
import java.util.Arrays;

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


    public void countWord(String pathToFile) {
        String outputDirName = "C:\\Users\\Roman_Yelizarov\\workspace\\fine_food_reviewer\\bd_store\\result.txt" ;

        SparkConf conf = new SparkConf();
        conf.setAppName(WorkerSupervisorActor.class.getName());
        conf.setMaster("local[*]");

        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<String> textFile = context.textFile(pathToFile);
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
        counts.saveAsTextFile(outputDirName);
    }



}
