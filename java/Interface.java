import java.io.IOException;
import java.util.Arrays;
import java.lang.reflect.Array;
import org.apache.lucene.queryparser.classic.ParseException;



public class Interface {
    public static void main(String[] args) throws IOException, IllegalArgumentException, ParseException {
        if (Array.getLength(args) == 0) {
            System.out.println("Please provide which mode you want to run in.");
        }
        else if ("indexer".equals(args[0])) {
            if (Array.getLength(args) == 3) {
                Indexer.main(Arrays.copyOfRange(args, 1, Array.getLength(args)));
            }
            else {
                System.out.println("Please provide the input and output directory.");
            } 
            
        }
        else if ("querier".equals(args[0])) {
            if (Array.getLength(args) > 3) {
                try {
                    Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Max result amount is not a number");
                    return;
                }
                Querier.main(Arrays.copyOfRange(args, 1, Array.getLength(args)));
            }
            else {
                System.out.println("Please provide the index directory and search terms you want to use.");
            } 
        }
        else {
            System.out.println("Unsupported mode " + args[0] + ".");
        } 
        
    }
}
