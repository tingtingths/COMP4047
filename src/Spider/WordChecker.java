package Spider;

import edu.smu.tspell.wordnet.*;

public class WordChecker {

	public WordChecker() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isAWord(String word) {

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsetNoun = database.getSynsets(word, SynsetType.NOUN);
		Synset[] synsetVerb = database.getSynsets(word, SynsetType.VERB);
//		System.err.println(synsetNoun.length + synsetVerb.length);
		
		if (synsetNoun.length + synsetVerb.length > 0)
			return true;
		else
			return false;

	}

	public static void main(String args[]) {
		System.out.println(isAWord("hong kong"));
	}

}
