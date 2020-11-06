import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.lucene.queryparser.classic.ParseException;

import java.lang.reflect.Array;


public class Interface {
    public static void main(String[] args) throws IOException, ParseException {
        if (Array.getLength(args) == 0) {
            System.out.println("Please provide which mode you want to run in.");
            return;
        }
        else if (args[0] == "indexer") {
            if (Array.getLength(args) == 3) {
                Indexer.main(Arrays.copyOfRange(args, 1, Array.getLength(args)));
                return; 
            }
            System.out.println("Please provide the input and output directory.");
            return;
        }
        else if (args[0] == "querier") {
            if (Array.getLength(args) > 2) {
                Querier.main(Arrays.copyOfRange(args, 1, Array.getLength(args)));
                return;
            }
            System.out.println("Please provide the index directory and search terms you want to use.");
            return;
        }
        System.out.println("Unsupported mode.");
        return;
    }
}
