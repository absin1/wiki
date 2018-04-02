package ai.talentify.wiki;

public class WikiPage {
	public enum Fields {
		content, id, title, wikiMarkup, pageURL
	}

	private String content;
	private String id;
	private String title;
	private String wikiMarkup;
	private String pageURL;

	public WikiPage(String content, String id, String title, String wikiMarkup, String pageURL) {
		super();
		this.content = content;
		this.id = id;
		this.title = title;
		this.wikiMarkup = wikiMarkup;
		this.pageURL = pageURL;
	}

	public WikiPage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the wikiMarkup
	 */
	public String getWikiMarkup() {
		return wikiMarkup;
	}

	/**
	 * @param wikiMarkup
	 *            the wikiMarkup to set
	 */
	public void setWikiMarkup(String wikiMarkup) {
		this.wikiMarkup = wikiMarkup;
	}

	/**
	 * @return the pageURL
	 */
	public String getPageURL() {
		return pageURL == null ? "https://en.wikipedia.org/?curid=" + id : pageURL;
	}

	/**
	 * @param pageURL
	 *            the pageURL to set
	 */
	public void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}

	public String toString() {
		return id + "\t" + title + "\t" + content + "\t" + wikiMarkup + "\t" + pageURL;
	}
}
