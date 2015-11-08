package Spider;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordChecker {

    public WordChecker() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Check if the keyword is in dictionary
     * @param word - the keyword
     * @return <tt>true</tt> - if it is in dictionary, <tt>false</tt> - if not in dictionary
     */
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
}
