import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.*;

import java.io.*;

public class WebSpider extends Thread {
	public static final int DemoX = 10; // URL pool size
	public static final int DemoY = 100; // Processed URL pool size
	public static final String prefix = "http://";
	public static final String DemoURLString = "http://buwww.hkbu.edu.hk/eng/main/index.jsp";
	//public static final String DemoURLString = "https://tingths.tk";
	public static final Pattern hrefPattern = Pattern.compile("href=\"(.*?)\"");
	public static final String[] URLException = { ".pdf", "..", ".gif", ".png", ".jpg", ".ico", "javascript", "mailto",
			".css" };
	public static final String[] StopList = { "and", "the", "for", "did", "does", "are", "was", "were", "has", "have",
			"had", "that", "this", "these", "which", "whose", "who", "whom", "what", "why", "she", "they", "them", "to",
			"doing", "be", "return", "var", "please", "will", "well", "over", "size", "its", "need", "sin", "known",
			"means", "true", "false", "tai" };
	public static final String[] KeywordTag = { "<title", "<p", "<li", "<div", "<a" };
	public static LinkedList<String> ProceeedURLPool = new LinkedList<>();
	public static LinkedList<String> DomainPool = new LinkedList<>();

	private LinkedList<String> URLPool; // Stores the links find in current page
	private LinkedList<String> Keywords; // Stores the keywords of current page
	private LinkedList<WebSpider> spiderEggs;
	public static LinkedList<String> DeadLinkPool = new LinkedList<>(); // Stores
	// the
	// Dead
	// links
	private String urlString; // URL string of current page
	private URL url; // URL object of current page
	private String domain; // domain of current page
	private int x;
	private int y;

	private boolean isSuccessful = false;

	private int generation = 0;

	public WebSpider() { // Demo constructor
		urlString = DemoURLString;
		try {
			url = new URL(urlString);
			domain = getDomain(DemoURLString);
			System.out.println("Domain: " + domain);
			x = DemoX;
			y = DemoY;
			URLPool = new LinkedList<>();
			Keywords = new LinkedList<>();
			spiderEggs = new LinkedList<>();
		} catch (MalformedURLException e) {
			System.out.println("Invalid URL!");
		}
	}

	public WebSpider(String inputURL, int X, int Y, int generation) {
		urlString = inputURL;
		try {
			url = new URL(urlString);
			domain = getDomain(inputURL);
			x = X;
			y = Y;
			URLPool = new LinkedList<>();
			Keywords = new LinkedList<>();
			spiderEggs = new LinkedList<>();
			this.generation = generation; // thread test
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
			e.printStackTrace();
		}
	}

	public void run() {
		//alternativeRun();
		if (true) {
			System.err.println("Spider born @ \"" + urlString + "\", generation " + generation + "<<<<<<<<<<<<<<<<<<<<<<<<");
			String[] strs = domain.split("\\.");

			if (strs.length > 1) {
				if (strs[0].contains("www"))
					this.Keywords.add(strs[1]);
				else
					this.Keywords.add(strs[0]);
			}

			isSuccessful = false;
			if (url == null) {
				return;
			}
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(url.openStream()));
				String currentLine;
				boolean reachBody = false;
				long startTime = new Date().getTime(); // debug
				while ((currentLine = in.readLine()) != null) {
					if (currentLine.contains("<title>ERROR: The requested URL could not be retrieved</title>")) {
						System.err.println("A spider died accidently due to error page... RIP");
						return;
					}
					// Extract keywords
					extractKeywords(currentLine);
					// Extract hyperlinks
					if (!reachBody && currentLine.contains("</head>"))
						reachBody = true;
					if (reachBody)
						extractHyperlinks(currentLine);
				}
				// debug
				long endTime = new Date().getTime();
				System.err.println("Lines process time : " + ((endTime - startTime)) / 1000 + " seconds.");

				in.close();
				isSuccessful = true;
				// spiderReproduce(); //BFS
				System.out.println("A spider died peacefully... RIP");

				spiderReport();
			} catch (IOException e) {
				System.err.println("\nA spider died accidently due to IOException... RIP");
				return;
			}
			return;
		}
	}

	public void alternativeRun() {
		System.err.println("Spider born @ " + urlString + ", generation " + generation + "<<<<<<<<<<<<<<<<<<<<<<<<");
		String[] strs = domain.split("\\.");

		if (strs.length > 1) {
			if (strs[0].contains("www"))
				this.Keywords.add(strs[1]);
			else
				this.Keywords.add(strs[0]);
		}

		isSuccessful = false;
		if (url == null) {
			return;
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String currentLine;
			String html = "";

			long startTime = new Date().getTime(); // debug

			while ((currentLine = in.readLine()) != null) {
				html += currentLine;
				extractKeywords(currentLine);
			}
			Document doc = Jsoup.parse(html);
			Elements links = doc.getElementsByTag("a");
			for (Element link : links) {
				String url = link.attr("href");

				while (spiderEggs.size() < x // BFS
						&& ProceeedURLPool.size() <= y && !URLPool.contains(url)
						&& !ProceeedURLPool.contains(url)
						&& !DeadLinkPool.contains(url)) {
					URLPool.add(url);
					if (URLPool.size() > 0 && spiderEggs.size() < x) {
						System.out.println(url);
						spiderEggs.add(new WebSpider(url, x, y, generation + 1));
						ProceeedURLPool.add(URLPool.remove());
						new WebSpider(url, x, y, generation + 1).run(); // BFS
					}
				}
			}

			// debug
			long endTime = new Date().getTime();
			System.err.println("Lines process time : " + ((endTime - startTime))/1000 + " seconds.");

			in.close();
			isSuccessful = true;
			// spiderReproduce(); //BFS
			System.out.println("A spider died peacefully... RIP");

			spiderReport();
		} catch (IOException e) {
			System.err.println("\nA spider died accidently due to IOException... RIP");
			return;
		}
		return;
	}

	private void spiderReport() {
		System.err.println(
				"===========================================================Spider report============================================================");
		System.err.println("Generation " + generation);
		System.out.println("==============URL======== \n" + this.urlString);
		System.out.println("===========Keywords====== ");
		for (int i = 0; i < this.Keywords.size(); i++)
			System.out.print("/ " + this.Keywords.get(i));
		System.err.println(
				"\n=====================================================================================================================================");
	}

	private void spiderReproduce() { // To be debugged, BFS search and handles
		// deadlinks
		try {
			int sucessSpider = 0;
			while (!spiderEggs.isEmpty() && sucessSpider < 10) {
				WebSpider offspring = spiderEggs.remove();
				offspring.run();
				offspring.join();
				if (offspring.getResult() == false) {
					DeadLinkPool.add(offspring.urlString);
					ProceeedURLPool.remove(offspring.urlString);
					DomainPool.remove(offspring.domain);
					if (!URLPool.isEmpty()) {
						spiderEggs.add(new WebSpider(URLPool.peek(), x, y, generation + 1));
						ProceeedURLPool.add(URLPool.remove());
					} else {
						sucessSpider++;
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void extractKeywords(String currentLine) {
		String currLine = currentLine.toLowerCase();
		String[] candidateKeywords = currLine.split(" ");
		if (isInStringArray(currLine, KeywordTag)) {
			try {
				for (int i = 0; i < candidateKeywords.length; i++) {
					if (WordChecker.isAWord(candidateKeywords[i]) && !isInStringArray(candidateKeywords[i], StopList)
							&& candidateKeywords[i].length() > 2 && !Keywords.contains(candidateKeywords[i])) {
						Keywords.add(candidateKeywords[i]);
					}

					if (i > 0 && !Keywords.contains(candidateKeywords[i - 1])) {
						String phrase = candidateKeywords[i - 1] + " " + candidateKeywords[i];
						if (WordChecker.isAWord(phrase))
							Keywords.add(phrase);
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
						&& !DeadLinkPool.contains(candidateString[i])) {
					URLPool.add(candidateString[i]);
					if (URLPool.size() > 0 && spiderEggs.size() < x) {
						System.out.println(candidateString[i]);
						spiderEggs.add(new WebSpider(candidateString[i], x, y, generation + 1));
						ProceeedURLPool.add(URLPool.remove());
						new WebSpider(candidateString[i], x, y, generation + 1).run(); // BFS
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

	public boolean getResult() {
		return isSuccessful;
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

	public void startSpider() {
		try {
			long startTime = new Date().getTime();
			run();
			join();
			long endTime = new Date().getTime();
			System.err.println("Process time : " + ((endTime - startTime)) / 1000 + " seconds.");
			System.out.println(
					"\n\n\n================ Final Report " + DomainPool.size() + " domains visited=====================");
			for (int i = 0; i < DomainPool.size(); i++)
				System.out.println(DomainPool.get(i));
			System.out.println();
			System.out.println("The number of webside has visited: " + ProceeedURLPool.size());
			for (int i = 0; i < ProceeedURLPool.size(); i++)
				System.out.println(ProceeedURLPool.get(i));
			System.out.println("===========================================");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// debug main
	public static void main(String[] args) {
		//System.out.println("cwd : " + System.getProperty("user.dir"));
		System.setProperty("wordnet.database.dir", System.getProperty("user.dir") + "\\dict");
		WebSpider spider = new WebSpider();
		spider.startSpider();
	}
}
