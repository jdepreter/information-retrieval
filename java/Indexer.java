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

public class Indexer {

    IndexWriter writer;
    Directory directory;
    IndexWriterConfig config;
    StandardAnalyzer standardAnalyzer;

    public Indexer(String relativeIndexPath) throws IOException {
        standardAnalyzer = new StandardAnalyzer();
        Path path = FileSystems.getDefault().getPath(".", relativeIndexPath);
        directory = FSDirectory.open(path);
        config = new IndexWriterConfig(standardAnalyzer);
        writer = new IndexWriter(directory, config);
    }

    public void addXMLDoc(String path) {
        try {
            Map<String, String> result = xmlReader.xmlToMap(path);
            Document doc = new Document();
            doc.add(new StoredField("path", path));
            doc.add(new TextField("questionTitle", result.get("questionTitle"), Field.Store.YES));
            doc.add(new TextField("questionBody", result.get("questionBody"), Field.Store.NO));
            doc.add(new TextField("answers", result.get("answers"), Field.Store.NO));
            doc.add(new TextField("acceptedAnswer", result.get("acceptedAnswer"), Field.Store.NO));
            addDocument(doc);

        } catch (SAXException | ParserConfigurationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addDocument(Document doc) throws IOException {
        writer.addDocument(doc);
    }

    public void close() throws IOException {
        writer.close();
    }

    /*
     * Stackoverflow query implementation: Idea: boost scores if term is in accepted
     * answer or in question
     */
    public void query(String queryString) throws IOException, ParseException {
        //
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        // QueryParser content = new QueryParser("content", standardAnalyzer);
        // QueryParser question = new QueryParser("question", standardAnalyzer);
        // QueryParser acceptedAnswer = new QueryParser("acceptAnswer",
        // standardAnalyzer);

        // Term term = new Term("path", queryString);
        // TermQuery q = new TermQuery(term);

        // PhraseQuery.Builder builder = new PhraseQuery.Builder();
        // builder.add(new Term("content", "lorem"), 4);
        // builder.add(new Term("content", "psum"), 5);
        // PhraseQuery pq = builder.build();

        // BooleanQuery.Builder booleanbuilder = new BooleanQuery.Builder();
        // booleanbuilder.add(pq, Occur.SHOULD);

        // TopDocs results = searcher.search(pq, 5);
        // System.out.println(results.totalHits);
        String[] temp = { "questionBody", "questionTitle", "answers", "acceptedAnswer" };
        Map<String, Float> mapding = new HashMap<String, Float>();
        mapding.put("questionBody", (float) 1);
        mapding.put("questionTitle", (float) 2);
        mapding.put("answers", (float) 1);
        mapding.put("acceptedAnswer", (float) 2);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(temp, standardAnalyzer, mapding);
        Query q = parser.parse(queryString);
        System.out.println(searcher.search(q, 5).scoreDocs[0]);
        // System.out.println(searcher.search(q, 5).scoreDocs[1]);
        
    }

    @Override
    public void finalize() {
        try {
            writer.close();
            System.out.println("Closed Indexwriter in the finalizer");
        } catch (IOException e) {
            // ...
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Indexer i = new Indexer("index");
        i.addXMLDoc("./dump/xml/63260067.xml");
        i.close();
        i.query("variables");
    }
}
