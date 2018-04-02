package ai.talentify.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import ai.talentify.wiki.WikiPage;

/**
 * This class creates an Apache Lucene index for all the wiki articles, it uses
 * titles, content and id as indexed information.
 */
public class IndexWiki {
	private static Logger logger = Logger.getLogger(IndexWiki.class.getName());
	private static StandardAnalyzer analyzer = new StandardAnalyzer();
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
	public IndexWiki(String indexDir) throws IOException {
		this.indexLocation = indexDir;
		// the boolean true parameter means to create a new index everytime,
		// potentially overwriting any existing files there.
		FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(dir, config);
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
	 * Indexes a file or directory
	 * 
	 * @param fileName
	 *            the name of a text file or a folder we wish to add to the index
	 * @throws java.io.IOException
	 *             when exception
	 */
	public void indexWikiArticle(WikiPage wikiPage) throws IOException {

		Document doc = new Document();
		doc.add(new Field(WikiPage.Fields.title.toString(), wikiPage.getTitle(), TextField.TYPE_STORED));
		doc.add(new Field(WikiPage.Fields.content.toString(), wikiPage.getContent(), TextField.TYPE_STORED));
		doc.add(new StringField(WikiPage.Fields.id.toString(), wikiPage.getId(), Field.Store.YES));
		try {
			writer.addDocument(doc);
			logger.info("Added wiki page with ID: " + wikiPage.getId() + " and title: " + wikiPage.getTitle());
		} catch (IOException e) {
			logger.error("Could not add: " + wikiPage.getId() + "because: \n" + e.getMessage(), e);
		}

	}
}
