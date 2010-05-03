import com.daohoangson.uml.parser.LexicalAnalyzer;

public class TestGrammar {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String source = "public static void main(String[] args) {";
		LexicalAnalyzer la = new LexicalAnalyzer(source, null);

		while (la.hasMoreElements()) {
			System.err.println(la.nextElement());
		}
	}

}
