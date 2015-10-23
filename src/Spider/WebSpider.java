package Spider;

import SearchEngine.Loghelper;

import java.net.*;
import java.util.LinkedList;
import java.util.regex.*;

import java.io.*;

public class WebSpider {
	public static final int DemoX = 10; // URL pool size
	public static final int DemoY = 100; // Processed URL pool size
	public static final String prefix = "http://";
	public static final String DemoURLString = "http://buwww.hkbu.edu.hk/eng/main/index.jsp";
	public static final Pattern hrefPattern = Pattern.compile("href=\"(.*?)\"");
	public static final String[] URLException = { ".pdf", "..", ".gif", ".png", ".jpg", ".ico", "javascript", "mailto",
			".css" };
	public static final String[] StopList = { "and", "the", "for", "did", "does", "are", "was", "were", "has", "have",
			"had", "that", "this", "these", "which", "whose", "who", "whom", "what", "why", "she", "they", "them", "to",
			"doing", "be", "return", "var", "please", "will", "well", "over", "size", "its", "need", "sin", "known",
			"means", "true", "false", "tai","nothing","can" };
	public static final String[] KeywordTag = { "<title", "<p", "<li", "<div", "<a", "<h", "<meta name=", "<menu", "<td","<u" };
	public static LinkedList<String> ProceeedURLPool;
	public static LinkedList<String> DomainPool = new LinkedList<>();

	private LinkedList<String> URLPool; // Stores the links find in current page
	private LinkedList<String> Keywords; // Stores the keywords of current page
	private LinkedList<KeywordNode> KeywordNodes;
	private LinkedList<WebSpider> spiderEggs;
	public static LinkedList<String> DeadLinkPool = new LinkedList<>(); // Stores
	// the
	// Dead
	// links
	private String title = ""; // page title
	private String urlString; // URL string of current page
	private URL url; // URL object of current page
	private String domain; // domain of current page
	private int x;
	private int y;
	private final int DomainKeywordRanking = 40;

	public WebSpider() { // Demo constructor
		urlString = DemoURLString;
		try {
			url = new URL(urlString);
			domain = getDomain(DemoURLString);
			System.out.println("Domain: " + domain);
			x = DemoX;
			y = DemoY;
			ProceeedURLPool = URLPoolStorage.get().pool;
			URLPool = new LinkedList<>();
			Keywords = new LinkedList<>();
			spiderEggs = new LinkedList<>();
			KeywordNodes = new LinkedList<>();
		} catch (MalformedURLException e) {
			System.out.println("Invalid URL!");
		}
	}

	public WebSpider(String inputURL, int X, int Y) {
		urlString = inputURL;
		try {
			url = new URL(urlString);
			domain = getDomain(inputURL);
			x = X;
			y = Y;
			ProceeedURLPool = URLPoolStorage.get().pool;
			URLPool = new LinkedList<>();
			Keywords = new LinkedList<>();
			spiderEggs = new LinkedList<>();
			KeywordNodes = new LinkedList<>();
		} catch (MalformedURLException e) {
			System.out.println("Invalid URL!");
		}
	}

	private String getDomain(String urlString) {
		if (url == null)
			return null;
		else {
			String domain = urlString.split("/")[2];
			if (domain.contains("?"))
				domain = domain.split("\\?")[0];
			if (!DomainPool.contains(domain))
				DomainPool.add(domain);
			return domain;
		}
	}

	public void printHTML() throws IOException {
		if (url == null)
			return;

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while ((str = in.readLine()) != null)
				System.out.println(str);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean spiderRun() {
		String[] strs = domain.split("\\.");

		if (strs.length > 1) {
			if (strs[0].contains("www")){
				this.Keywords.add(strs[1]);
				this.KeywordNodes.add(new KeywordNode(strs[1], DomainKeywordRanking));
			}
			else
			{
				this.Keywords.add(strs[0]);
				this.KeywordNodes.add(new KeywordNode(strs[0], DomainKeywordRanking));
			}
		}

		boolean isSuccessful = false;
		if (url == null) {
			return isSuccessful;
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String currentLine;
			boolean reachBody = false;
			while ((currentLine = in.readLine()) != null) {
				if (currentLine.contains("<title>ERROR: The requested URL could not be retrieved</title>")) {
					System.err.println("A spider died accidentally due to error page... RIP");
					return isSuccessful;
				}
				// Extract title
				Matcher m = Pattern.compile(".*<title>(.*)</title>.*").matcher(currentLine);
				if (title.trim().isEmpty() && m.matches()) {
					Loghelper.get().log("grab title", urlString + " -> " + currentLine);
					title = m.group(1);
				}
				// Extract keywords
				extractKeywords(currentLine);
				// Extract hyperlinks
				if (!reachBody && currentLine.contains("</head>"))
					reachBody = true;
				if (reachBody)
					extractHyperlinks(currentLine);
			}
			in.close();
			isSuccessful = true;
			//spiderReproduce(); //BFS
			System.out.println("A spider died peacefully... RIP");

			spiderReport();
		} catch (IOException e) {
			System.err.println("\nA spider died accidentally due to IOException... RIP");
			return isSuccessful;
		}
		return isSuccessful;
	}

	private void spiderReproduce() { // To be debugged, BFS search and handles
		// deadlinks
		System.out.println("There are " + spiderEggs.size() + " eggs.");
		/*
		 * int sucessSpider = 0; while (!spiderEggs.isEmpty() && sucessSpider <
		 * 10) { WebSpider offspring = spiderEggs.remove(); if
		 * (offspring.spiderRun() == false) {
		 * DeadLinkPool.add(offspring.urlString);
		 * ProceeedURLPool.remove(offspring.urlString);
		 * DomainPool.remove(offspring.domain); if (!URLPool.isEmpty()) {
		 * spiderEggs.add(new WebSpider(URLPool.peek(), x, y));
		 * ProceeedURLPool.add(URLPool.remove()); } else { sucessSpider++; } } }
		 */
	}

	private void extractKeywords(String currentLine) {
		String currLine = currentLine.toLowerCase();
		String[] candidateKeywords = currLine.split(" ");
		if (isInStringArray(currLine, KeywordTag)) {
			if(currentLine.contains("<") && currentLine.contains(">") && currentLine.toLowerCase().split(">").length > 1)
				currLine = currLine.split(">")[1].split("<")[0];
			try {
				for (int i = 0; i < candidateKeywords.length; i++) {

					if (WordChecker.isAWord(candidateKeywords[i]) && !isInStringArray(candidateKeywords[i], StopList)
							&& candidateKeywords[i].length() > 2) {

						if (!Keywords.contains(candidateKeywords[i])) {
							Keywords.add(candidateKeywords[i]);
							KeywordNodes.add(new KeywordNode(candidateKeywords[i], 1));
						} else {
							for (int m = 0; m < KeywordNodes.size(); m++) {
								if (KeywordNodes.get(m).keyword.equals(candidateKeywords[i])) {
									KeywordNodes.get(m).addCounter();
									break;
								}
							}
						}
					}

					if (i > 0) {
						String phrase = candidateKeywords[i - 1] + " " + candidateKeywords[i];
						// System.out.println("phrase : " + phrase);
						if (WordChecker.isAWord(phrase)) {
							if (!Keywords.contains(candidateKeywords[i - 1])) {
								Keywords.add(phrase);
								KeywordNodes.add(new KeywordNode(phrase, 1));
							} else {
								for (int m = 0; m < KeywordNodes.size(); m++) {
									if (KeywordNodes.get(m).keyword.equals(phrase)) {
										KeywordNodes.get(m).addCounter();
										break;
									}
								}
							}
							// System.out.println("catch phrase : " + phrase);
						}

					}

				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
	}

	private void extractHyperlinks(String currentLine) {
		String[] candidateString = getCandidateURLString(currentLine);
		if (candidateString != null) // and the URL pool is not full
		{
			for (int i = 0; i < candidateString.length && candidateString[i] != null; i++) {
				if (candidateString[i].contains("#")) {
					if (candidateString[i].equals("#"))
						break;
					else
						candidateString[i] = candidateString[i].split("#")[0];
				}

				if (candidateString[i].contains("<"))
					candidateString[i] = candidateString[i].split("<")[0];

				if (!candidateString[i].contains("http"))
					candidateString[i] = prefix + domain + candidateString[i];

				while (spiderEggs.size() < x // BFS
						&& ProceeedURLPool.size() <= y && !URLPool.contains(candidateString[i])
						&& !ProceeedURLPool.contains(candidateString[i])
						&& !DeadLinkPool.contains(candidateString[i])
						&& !hasSame(ProceeedURLPool, candidateString[i])) {
					URLPool.add(candidateString[i]);
					Loghelper.get().log(this.getClass().getSimpleName(), "pool size : " + URLPool.size());
					if (URLPool.size() > 0 && spiderEggs.size() < x) {
						//System.out.println(candidateString[i]);
						spiderEggs.add(new WebSpider(candidateString[i], x, y));
						ProceeedURLPool.add(URLPool.remove());
						new WebSpider(candidateString[i], x, y).spiderRun(); // Now is DFS, to be changed into BFS
					}
				}
			}
		}
	}

	private String[] getCandidateURLString(String currentLine) {
		if (currentLine.contains("<a href")) {

			int candidateSize = currentLine.split("href").length - 1;
			String[] candidateString = new String[candidateSize];
			Matcher urlMatcher = hrefPattern.matcher(currentLine);

			for (int i = 0; i < candidateSize && urlMatcher.find(); i++) {
				candidateString[i] = urlMatcher.group(1);

				if (isInStringArray(candidateString[i], URLException))
					candidateString[i] = null;
			}
			return candidateString;
		} else
			return null;
	}

	private boolean isInStringArray(String str, String[] strArray) {
		boolean isIn = false;
		for (int i = 0; i < strArray.length; i++) {
			if (str.contains(strArray[i])) {
				isIn = true;
			}
		}
		return isIn;
	}

	private boolean hasSame(LinkedList<String> pool, String url) {
		//Loghelper.get().log(this.getClass().getSimpleName(), pool.size() + "--------------------------------------------------------");
		//Loghelper.get().log(this.getClass().getSimpleName(), "url : " + url);
		URL testURL;
		try {
			testURL = new URL(url);

			for (String poolUrl : pool) {
				//Loghelper.get().log(this.getClass().getSimpleName(), "		: " + pool);

				URL targetURL = new URL(poolUrl);
				if (targetURL.getHost().toLowerCase().equals(testURL.getHost().toLowerCase())
						&& targetURL.getHost().toLowerCase().equals(testURL.getHost().toLowerCase())) {
					return true;
				}
			}
		} catch (MalformedURLException e) { }
		return false;
	}

	/*
	public static void main(String[] args) {
		System.setProperty("wordnet.database.dir", "C:\\Users\\e4206692\\Desktop\\COMP4047\\dict");
		WebSpider s = new WebSpider();
		s.spiderRun();
		System.out.println(
				"\n\n\n================ Final Report " + DomainPool.size() + " domains visited=====================");
		for (int i = 0; i < DomainPool.size(); i++)
			System.out.println(DomainPool.get(i));
		System.out.println();
		System.out.println("The number of webside has visited: " + ProceeedURLPool.size());
		for (int i = 0; i < ProceeedURLPool.size(); i++)
			System.out.println(ProceeedURLPool.get(i));
		System.out.println("===========================================");
	}
	*/

	private void spiderReport() {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("user.dir") + File.separator + "spiderResult.txt", true)))) {
			out.print(this.domain);
			out.print(";");
			out.print(this.urlString);
			out.print(";");
			if (title.trim().isEmpty()) title = "null";
			out.print(title + ";");
			for (int i = 0; i < this.Keywords.size(); i++)
				out.print("/ " + this.Keywords.get(i) + ":" + this.KeywordNodes.get(i).counter);
			out.println();
		}catch (IOException e) {
			//exception handling left as an exercise for the reader
		}


		System.err.println(
				"===========================================================Spider report============================================================");
		System.out.println("==============URL======== \n" + this.urlString);
		System.out.println("===========Keywords====== ");
		for (int i = 0; i < this.Keywords.size(); i++)
			System.out.print("/ " + this.Keywords.get(i) + ":" + this.KeywordNodes.get(i).counter);
		System.err.println(
				"\n=====================================================================================================================================");
	}

	public void startSpider() {
		spiderRun();
		System.out.println(
				"\n\n\n================ Final Report " + DomainPool.size() + " domains visited=====================");
		for (int i = 0; i < DomainPool.size(); i++)
			System.out.println(DomainPool.get(i));
		System.out.println();
		System.out.println("The number of webside has visited: " + ProceeedURLPool.size());
		for (int i = 0; i < ProceeedURLPool.size(); i++)
			System.out.println(ProceeedURLPool.get(i));
		System.out.println("===========================================");
	}
}

class KeywordNode
{
	String keyword;
	int counter;
	public KeywordNode(String keywd, int cou)
	{
		this.keyword = keywd;
		counter = cou;
	}

	public void addCounter() {
		counter++;
	}
}
