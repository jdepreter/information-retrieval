import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;


public class Querier {
    
    Directory directory;
    StandardAnalyzer standardAnalyzer;

    public Querier(String relativeIndexPath) throws IOException {
        standardAnalyzer = new StandardAnalyzer();
        Path path = FileSystems.getDefault().getPath(".", relativeIndexPath);
        directory = FSDirectory.open(path);
    }

      /*
     * Stackoverflow query implementation: Idea: boost scores if term is in accepted
     * answer or in question
     */
    public void query(String queryString) throws IOException, ParseException {
        //
        System.out.println("-------------------");
        System.out.println("Query: " + queryString);
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        
        String[] temp = { "questionBody", "questionTitle", "answers", "acceptedAnswer" };
        Map<String, Float> mapding = new HashMap<String, Float>();
        mapding.put("questionBody", (float) 1);
        mapding.put("questionTitle", (float) 2);
        mapding.put("answers", (float) 1);
        mapding.put("acceptedAnswer", (float) 2);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(temp, standardAnalyzer, mapding);
        Query q = parser.parse(queryString);
        System.out.println(q.toString());
        TopDocs results = searcher.search(q, 5);
        System.out.println("-------------------");
        for (int i = 0; i < results.totalHits.value; i++) {
            System.out.println(results.scoreDocs[i]);
            System.out.println(reader.document(results.scoreDocs[i].doc).get("path"));
            System.out.println(reader.document(results.scoreDocs[i].doc).get("questionTitle"));
            System.out.println("-------------------");
        }
        
        // System.out.println(searcher.search(q, 5).scoreDocs[1]);
        
    }

    public static void main(String[] args) throws IOException, ParseException {
        Querier q = new Querier("index");
        q.query("const");
    }
}   
