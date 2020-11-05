import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.lang.reflect.Array; 

import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.TermQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.TermsQueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.xml.sax.SAXException;

public class Querier {

    Directory directory;
    Analyzer standardAnalyzer;
    String[] fields = { "questionBody", "questionTitle", "answers", "acceptedAnswer" };
    // String[] fields = { "questionTitle", "questionBody" };

    public Querier(String relativeIndexPath) throws IOException {
        standardAnalyzer = new SimpleAnalyzer();
        Path path = FileSystems.getDefault().getPath(".", relativeIndexPath);
        directory = FSDirectory.open(path);
    }

    /*
     * Stackoverflow query implementation: Idea: boost scores if term is in accepted
     * answer or in question
     */

    private Query MultiFieldQuery(String[] queryString) throws ParseException, IOException {
        String[] temp = { "questionBody", "questionTitle", "answers", "acceptedAnswer" };
        Map<String, Float> mapding = new HashMap<String, Float>();
        mapding.put("questionBody", (float) 1);
        mapding.put("questionTitle", (float) 2);
        mapding.put("answers", (float) 1);
        mapding.put("acceptedAnswer", (float) 2);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(temp, standardAnalyzer, mapding);
        Query q = parser.parse(String.join(" ", queryString));
        System.out.println(q.toString());
        return q;
    }

    private Query BooleanQ(String[] searchTerms) throws IOException, ParseException {

        BooleanQuery.Builder b = new BooleanQuery.Builder();

        // String[] searchTerms = queryString.split(" ");
        for (String field : fields) {
            BooleanQuery.Builder fieldBoolBuilder = new BooleanQuery.Builder();
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            QueryParser qp = new QueryParser(field, standardAnalyzer);
            Query finalQuery;
            for (int i = 0; i < searchTerms.length; i++) {

                // Fill PhraseQueryBuilder
                builder.add(new Term(field, searchTerms[i]), i);

                // Maybe some optional handles like "" and -
                if (searchTerms[i].startsWith("-")) {
                    Query tq = qp.parse(searchTerms[i].substring(1));
                    fieldBoolBuilder.add(tq, BooleanClause.Occur.MUST_NOT);
                } else if (searchTerms[i].startsWith("\"") && searchTerms[i].endsWith("\"")) {
                    Query tq = qp.parse(searchTerms[i].replace("\"", ""));
                    fieldBoolBuilder.add(tq, BooleanClause.Occur.MUST);
                } else {
                    Query tq = qp.parse(searchTerms[i]);
                    fieldBoolBuilder.add(tq, BooleanClause.Occur.SHOULD);
                }
            }

            PhraseQuery phraseQuery = builder.build();
            finalQuery = fieldBoolBuilder.build();
            

            // fieldBoolBuilder.add(phraseQuery, BooleanClause.Occur.SHOULD);
            if (field == "questionTitle" || field == "acceptedAnswer") {
                finalQuery = new BoostQuery(finalQuery, (float)2.0);
            }
            b.add(finalQuery, BooleanClause.Occur.SHOULD);

        }
        Query q = b.build();
        System.out.println(q.toString());
        return q;
    }

    public void query(String[] queryStringArray) throws IOException, ParseException {
        //
        System.out.println("-------------------");
        System.out.println("Query: " + Arrays.toString(queryStringArray));
        
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        // searcher.setSimilarity(new ClassicSimilarity());

        TopDocs results = searcher.search(BooleanQ(queryStringArray), 69);
        
        System.out.println("-------------------");
        System.out.println("Total amount: " + results.totalHits.value);
        System.out.println("-------------------");
        for (int i = 0; i < Math.min(results.totalHits.value, 69); i++) {
            System.out.println(results.scoreDocs[i]);
            System.out.println(reader.document(results.scoreDocs[i].doc).get("path"));
            System.out.println(reader.document(results.scoreDocs[i].doc).get("questionTitle"));
            System.out.println("-------------------");
        }

        // System.out.println(searcher.search(q, 5).scoreDocs[1]);

    }

    public static void main(String[] args) throws IOException, ParseException {
        Querier q = new Querier(args[0]);
        q.query(Arrays.copyOfRange(args, 1, Array.getLength(args)));
    }
}
