import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;

public class Indexer {

    IndexWriter writer;
    Directory directory;
    IndexWriterConfig config;
    Analyzer standardAnalyzer;

    public Indexer(String relativeIndexPath) throws IOException {
        standardAnalyzer = new SimpleAnalyzer();
        Path path = FileSystems.getDefault().getPath(".", relativeIndexPath);
        directory = FSDirectory.open(path);
        config = new IndexWriterConfig(standardAnalyzer);
        // config.setSimilarity(new ClassicSimilarity());
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
            e.printStackTrace();
        }
    }

    public void addDocument(Document doc) throws IOException {
        writer.addDocument(doc);
    }

    public void close() throws IOException {
        writer.close();
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

        String[] pathnames;
        String dirPath = args[0];
        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File f = new File(dirPath);

        // Populates the array with names of files and directories
        pathnames = f.list();

        Indexer i = new Indexer(args[1]);
        // For each pathname in the pathnames array
        for (String pathname : pathnames) {
            // Print the names of files and directories
            // System.out.println(pathname);

            i.addXMLDoc(dirPath + pathname);
            
        }

        i.close();
    }
}
