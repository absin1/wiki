/**
 * 
 */
package ai.talentify.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;

import ai.talentify.wiki.WikiPage;

/**
 * @author absin
 *
 */
public class SearchWikiTest {
	public static void main(String[] args) {
		new SearchWikiTest().runTest();
	}

	private void runTest() {
		SearchWiki searchWiki = null;
		Long currentTimeMillis = null;
		try {
			searchWiki = new SearchWiki("E:\\WikiPedia\\LuceneIndex");
			File file = new File("E:\\WikiPedia\\Logs\\AI.log.2018-03-30");
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					String[] split = line.split(": ");
					String ID = split[1].trim().split(" ")[0];
					String searchTerm = split[2].trim().trim();
					boolean passed = false;
					currentTimeMillis = System.currentTimeMillis();
					ArrayList<Document> searchByTitle = searchWiki.searchByTitle(searchTerm, 1000);
					for (Document document : searchByTitle) {
						if (document.get(WikiPage.Fields.id.toString()).equalsIgnoreCase(ID)) {
							passed = true;
							break;
						}
					}
					System.err.println("Searching for title: " + searchTerm + " finished in: "
							+ (System.currentTimeMillis() - currentTimeMillis) + "with result: " + passed);

				}
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
