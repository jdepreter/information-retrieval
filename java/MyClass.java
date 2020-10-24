import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MyClass {

  public static void main(String[] args) throws IOException, ParseException {
    // New index
    StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
    Path path = FileSystems.getDefault().getPath(".", "index");
    Directory directory = FSDirectory.open(path);

    IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
    // Create a writer
    IndexWriter writer = new IndexWriter(directory, config);
    Document document = new Document();
    // In a real world example, content would be the actual content that needs to be
    // indexed.
    // Setting content to Hello World as an example.
    String inputFilePath = "./lorem.txt";
    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {

        // document.add(new TextField("content", br));
        // document.add(new TextField("path", "./lorem.txt", Field.Store.YES));
        // writer.addDocument(document);
        // writer.close();

    } catch (IOException e) {
        e.printStackTrace();
    }
    // writer.addDocument(document);
    // writer.close();

    // Now let's try to search for Hello
    IndexReader reader = DirectoryReader.open(directory);
    IndexSearcher searcher = new IndexSearcher(reader);
    QueryParser parser = new QueryParser("content", standardAnalyzer);
    Query query = parser.parse("Hello");
    TopDocs results = searcher.search(query, 5);
    System.out.println("Hits for Hello -->" + results.totalHits);

    // case insensitive search
    query = parser.parse("hello");
    results = searcher.search(query, 5);
    System.out.println("Hits for hello -->" + results.totalHits);

    // search for a value not indexed
    query = parser.parse("Hi there");
    results = searcher.search(query, 5);
    System.out.println("Hits for Hi there -->" + results.totalHits);

    // search for a value not indexed
    query = parser.parse("lorem");
    results = searcher.search(query, 5);
    System.out.println("Hits for lorem -->" + results.totalHits);
    System.out.println(results.scoreDocs[0]);
    System.out.println(results.scoreDocs[1]);
    System.out.println(reader.document(results.scoreDocs[0].doc).get("path"));
    System.out.println(reader.document(results.scoreDocs[1].doc).get("path"));
    System.out.println(reader.document(results.scoreDocs[1].doc).get("content"));
  }
}
