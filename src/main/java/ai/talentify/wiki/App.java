package ai.talentify.wiki;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.log4j.Logger;
import org.wikiclean.WikiClean;
import org.wikiclean.WikiClean.WikiLanguage;

import ai.talentify.lucene.IndexWiki;
import fastily.jwiki.core.Wiki;

/**
 * Hello world!
 *
 */
public class App {
	private static Logger logger = Logger.getLogger(App.class.getName());

	public static void main(String[] args) {
		// wikitionary();
		// wikipedia();
		processWikiDumpXMLLocally();
		// processWikiDumpCompressedLocally();
		// showCompressedFileBatchWise(10);
	}

	private static void showCompressedFileBatchWise(int totalLineCount) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = getBufferedReaderForCompressedFile(
					"E:\\WikiPedia\\enwiki-20180320-pages-articles-multistream.xml.bz2");
		} catch (FileNotFoundException | CompressorException e) {
			e.printStackTrace();
		}
		int lineCount = 0;
		String line = null;
		StringBuilder batchPara = new StringBuilder();
		try {
			line = bufferedReader.readLine();
			while (line != null) {
				lineCount++;
				batchPara.append(line + "\n");
				if (totalLineCount == lineCount) {
					lineCount = 0;
					System.out.println(batchPara);
					batchPara.delete(0, batchPara.length() - 1);
				}
				line = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void processWikiDumpCompressedLocally() {
		WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiLanguage.EN).withTitle(true).withFooter(true)
				.build();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = getBufferedReaderForCompressedFile(
					"E:\\WikiPedia\\enwiki-20180320-pages-articles-multistream.xml.bz2");
		} catch (FileNotFoundException | CompressorException e) {
			e.printStackTrace();
		}
		String line = null;
		IndexWiki indexWiki = null;
		try {
			indexWiki = new IndexWiki("E:\\WikiPedia\\LuceneIndex");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Boolean pageStart = false;
			Boolean pageEnd = false;
			String raw = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().equalsIgnoreCase("<page>"))
					pageStart = true;
				if (line.trim().equalsIgnoreCase("</page>"))
					pageEnd = true;
				if (pageStart)
					raw += line + "\n";
				if (pageEnd) {
					String content = cleaner.clean(raw);
					String id = cleaner.getId(raw);
					String title = cleaner.getTitle(raw);
					String wikiMarkup = cleaner.getWikiMarkup(raw);
					String pageURL = "https://en.wikipedia.org/?curid=" + id;
					WikiPage wikiPage = new WikiPage(content, id, title, wikiMarkup, pageURL);
					indexWiki.indexWikiArticle(wikiPage);
					raw = "";
					pageEnd = false;
					pageStart = false;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (indexWiki != null)
			try {
				indexWiki.closeIndex();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private static void processWikiDumpXMLLocally() {
		WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiLanguage.EN).withTitle(true).withFooter(true)
				.build();
		File file = new File("E:\\WikiPedia\\enwiki-20180320-pages-articles-multistream.xml");
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			logger.error("Opening xml failed because: " + e1.getMessage(), e1);
		}
		String line = null;
		IndexWiki indexWiki = null;
		try {
			indexWiki = new IndexWiki("E:\\WikiPedia\\LuceneIndex");
		} catch (IOException e1) {
			logger.error("Opening index failed because: " + e1.getMessage(), e1);
		}
		try {
			Boolean pageStart = false;
			Boolean pageEnd = false;
			String raw = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().equalsIgnoreCase("<page>"))
					pageStart = true;
				if (line.trim().equalsIgnoreCase("</page>"))
					pageEnd = true;
				if (pageStart)
					raw += line + "\n";
				if (pageEnd) {
					try {
						String content = cleaner.clean(raw);
						String id = cleaner.getId(raw);
						String title = cleaner.getTitle(raw);
						String wikiMarkup = cleaner.getWikiMarkup(raw);
						String pageURL = "https://en.wikipedia.org/?curid=" + id;
						WikiPage wikiPage = new WikiPage(content, id, title, wikiMarkup, pageURL);
						try {
							indexWiki.indexWikiArticle(wikiPage);
						} catch (Exception e) {
							logger.error("Indexing failed for page: " + id + " because: " + e.getMessage() + "\n"
									+ "for article: " + wikiPage.toString(), e);
						}
					} catch (IllegalArgumentException e) {
						logger.error(e.getMessage(), e);
					}
					raw = "";
					pageEnd = false;
					pageStart = false;
				}
			}
		} catch (Exception e1) {
			logger.error("Reading lines from bufferedreader failed because: " + e1.getMessage(), e1);
		}
		if (indexWiki != null)
			try {
				indexWiki.closeIndex();
			} catch (IOException e1) {
				logger.error("Closing lucene index failed because: " + e1.getMessage(), e1);
			}
	}

	private static String readFile(String string) throws IOException {
		File file = new File(string);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null)
			builder.append(line);
		return builder.toString();
	}

	private static void wikipedia() {
		Wiki wiki = new Wiki("en.wikipedia.org");
		String title = "Java (programming language)";
		ArrayList<String> linksOnPage = wiki.getLinksOnPage(title);
		for (String string : linksOnPage) {
			System.out.println(string);

		}
	}

	private static void wikitionary() {
		Wiki wiki = new Wiki("en.wiktionary.org");
		String title = "car";
		// System.out.println(wiki.getPageText(title));
		// System.out.println(wiki.getImagesOnPage(title).toString());
		/*-ArrayList<PageSection> splitPageByHeader = wiki.splitPageByHeader(title);
		for (PageSection pageSection : splitPageByHeader) {
			System.out.println(pageSection.header);
			System.err.println(pageSection.text);
		}*/
	}

	private static BufferedReader getBufferedReaderForCompressedFile(String fileIn)
			throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
		return br2;
	}
}
