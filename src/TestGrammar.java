import com.daohoangson.uml.parser.GrammarOfJava;
import com.daohoangson.uml.parser.LexicalAnalyzer;

/**
 * Test for {@link GrammarOfJava}
 * 
 * @author Dao Hoang Son
 * @version 1.1
 * 
 */
public class TestGrammar {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String source = "public static void main(String[] args) {";
		LexicalAnalyzer la = new LexicalAnalyzer(source, new GrammarOfJava());

		while (la.hasMoreElements()) {
			System.err.println(la.nextElement());
		}
	}

}
