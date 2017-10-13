package spark;


import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import play.Logger;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class SparkService {

    public void countWord() {
        String inputFileName = "C:\\Users\\Roman_Yelizarov\\workspace\\fine_food_reviewer\\bd_store\\Reviews.csv";
        String outputDirName = "C:\\Users\\Roman_Yelizarov\\workspace\\fine_food_reviewer\\bd_store\\result";

        long startTime = System.nanoTime();

        SparkConf conf = new SparkConf();
        conf.setAppName("org.sparkexample.WordCount");
        conf.setMaster("local");

        JavaSparkContext context = new JavaSparkContext(conf);


        SQLContext sqlContext = new SQLContext(context);

        System.out.println("======= level 2 ======");

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("header", "true");
        options.put("path", inputFileName);

        JavaRDD<Row> file = sqlContext.load("com.databricks.spark.csv", options).javaRDD();

        System.out.println("======= level 3 ======");

/*

        SparkSession spark = SparkSession.builder()
                .appName(SparkService.class.getName())
                .config("org.sparkexample.config", "value")
                .getOrCreate();

        JavaRDD<Row> file = spark.read().format("com.databricks.spark.csv")
                .option("haeder", "true")
                .load(inputFileName).javaRDD();*/

        JavaPairRDD<String, Integer> result = file.flatMap(s -> Arrays.asList(s.getString(9).split(" ")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);


        System.out.println("======= level 4 ======");

/*
        JavaRDD<String> textFile = context.textFile(inputFileName);
        JavaRDD<String> countPrep = textFile.flatMap(s -> Arrays.asList(s.split(" ")).iterator());
        JavaPairRDD<String, Integer> countMap = countPrep
                .mapToPair(word -> new Tuple2<>(word, 1));
        JavaPairRDD<String, Integer> countReduce = countMap.reduceByKey((a, b) -> a + b);*/

        Map<String, Integer> mapWords = result.collectAsMap();
        LinkedHashMap<String, Integer> newMap = mapWords.entrySet().parallelStream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(100)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new
                ));
        Logger.debug("Parse file finished. spent: {}", (System.nanoTime() - startTime) / 1000);


    }



  /*  public static void ain(String[] args) {

        String inputFileName = "samples/big.txt" ;
        String outputDirName = "output" ;

        SparkConf conf = new SparkConf().setAppName("org.sparkexample.WordCount").setMaster("local");
        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<String> file = context.textFile(inputFileName);
        JavaRDD<String> words = file.flatMap(WORDS_EXTRACTOR);
        JavaPairRDD<String, Integer> pairs = words.mapToPair(WORDS_MAPPER);
        JavaPairRDD<String, Integer> counter = pairs.reduceByKey(WORDS_REDUCER);

        counter.saveAsTextFile(outputDirName);
    }*/
}
