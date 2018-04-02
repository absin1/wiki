package ai.talentify.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

import ai.talentify.wiki.WikiPage;

public class SearchWiki {
	private static Logger logger = Logger.getLogger(SearchWiki.class.getName());
	private static StandardAnalyzer analyzer = new StandardAnalyzer();
	private static IndexReader reader;
	private static IndexSearcher searcher;
	private IndexWriter writer;
	String indexLocation;

	/**
	 * Constructor
	 * 
	 * @param indexDir
	 *            the name of the folder in which the index should be created
	 * @throws java.io.IOException
	 *             when exception creating index.
	 */
	public SearchWiki(String indexDir) throws IOException {
		this.indexLocation = indexDir;
		// the boolean true parameter means to create a new index everytime,
		// potentially overwriting any existing files there.
		FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// writer = new IndexWriter(dir, config);
		reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation).toPath()));
		searcher = new IndexSearcher(reader);
	}

	/**
	 * Close the index.
	 * 
	 * @throws java.io.IOException
	 *             when exception closing
	 */
	public void closeIndex() throws IOException {
		writer.close();
	}

	/**
	 * Searches the indexed wiki by the title of wikipage and returns the indexed
	 * lucene documents as a list limiting the maximum hits to the count. To get
	 * more details from the lucene document refer the WikiPage.Fields Enum
	 * 
	 * @param searchQuery
	 * @param count
	 * @return
	 */
	public ArrayList<Document> searchByTitle(String searchQuery, Integer count) {
		return searchByAttribute(searchQuery, WikiPage.Fields.title.toString(), count);
	}

	/**
	 * Searches the indexed wiki by document attribute like title or content or ID
	 * and returns a maximum count entries found
	 * 
	 * @param searchQuery
	 * @param attribute
	 * @param count
	 * @return
	 */
	private ArrayList<Document> searchByAttribute(String searchQuery, String attribute, Integer count) {
		ArrayList<Document> documents = new ArrayList<>();
		TopScoreDocCollector collector = TopScoreDocCollector.create(count);
		try {
			Query q = new QueryParser(attribute, analyzer).parse(searchQuery);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			logger.info("Search for: " + searchQuery + " in Field: " + attribute + " Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				documents.add(d);
			}

		} catch (Exception e) {
			logger.info("Error searching " + searchQuery + " : " + e.getMessage());
		}
		return documents;
	}
}
