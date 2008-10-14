package org.jdna.media.metadata.impl.dvdprof;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.VideoSearchResult;
import org.jdna.url.CookieHandler;

public class MovieIndex {
	private static final Logger log = Logger.getLogger(MovieIndex.class);
	private static MovieIndex indexer = new MovieIndex();
	
	private IndexReader reader = null;
	private IndexWriter writer = null;
	private File indexDir = null;
	
    private Searcher searcher = null;
    private Analyzer analyzer = new StandardAnalyzer();
    private QueryParser parser = new QueryParser("title", analyzer);
	
	public static MovieIndex getInstance() {
		return indexer;
	}
	
	public boolean isNew() {
		File ch[] = getIndexDir().listFiles();
		return ch==null || ch.length==0;
	}
	
	public void beginIndexing() throws Exception {
		writer = new IndexWriter(getIndexDir(), new StandardAnalyzer(), true);
	}
	
	public void endIndexing() throws Exception {
		writer.optimize();
		writer.close();
		
		// open the index, for searching
		openIndex();
	}
	
	public void openIndex() throws Exception {
		indexDir  = getIndexDir();
		if (!indexDir.exists()) {
			log.debug("Creating Lucene Index Dir: " + indexDir.getAbsolutePath());
		}
		
		log.debug("Opening Lucene Index: " + indexDir.getAbsolutePath());
		
		reader = IndexReader.open(indexDir);
		searcher = new IndexSearcher(reader);
	}

	public void addMovie(String name, String date, String url) throws Exception {
		log.debug("Indexing Movie: " + name + "; date: " + date + "; url: " + url);
		Document doc = createDocument(name, date, url);
		writer.addDocument(doc);
	}
	
	private File getIndexDir() {
		String sdir = ConfigurationManager.getInstance().getProperty("org.jdna.media.metadata.impl.dvdprof.MovieIndex.indexDir", "cache/index");
		File dir = new File(sdir);
		if (!dir.exists()) {
			log.debug("Creating Lucene Index Dir: " + dir.getAbsolutePath());
		}
		return dir;
	}

	public static Document createDocument(String name, String date, String url) {
	    // make a new, empty document
	    Document doc = new Document();

	    // index titles
	    doc.add(new Field("title", name, Field.Store.YES, Field.Index.TOKENIZED));

	    // Store release date but not index
	    doc.add(new Field("release", date, Field.Store.YES, Field.Index.NO));
	    doc.add(new Field("url", url, Field.Store.YES, Field.Index.NO));

	    // return the document
	    return doc;
	}
	
	
	public List<IVideoSearchResult> searchTitle(String title, CookieHandler handler) throws Exception {
		if (searcher==null) openIndex();
		
		Query query = parser.parse(title);
		Hits hits = searcher.search(query);
		
		int l = hits.length();
		List<IVideoSearchResult> results = new ArrayList<IVideoSearchResult>(l);
		
		for (int i=0;i<l;i++) {
			Document d = hits.doc(i);
			int type=IVideoSearchResult.RESULT_TYPE_UNKNOWN;
			if (hits.score(i)>0.99) {
				type = IVideoSearchResult.RESULT_TYPE_EXACT_MATCH;
			} else if(hits.score(i)>0.9) {
				type = IVideoSearchResult.RESULT_TYPE_POPULAR_MATCH;
			}
			
			String name = d.get("title");
			String date = d.get("release");
			String url = d.get("url");
			
			results.add(new VideoSearchResult(DVDProfMetaDataProvider.PROVIDER_ID, url, name, date, type));
		}
		
		
		return results;
		
	}

	public void clean() {
		if (isNew()) return;
		
		log.debug("Deleting All Currently indexed documents.");
		try {
			openIndex();
			int s = reader.numDocs();
			for (int i=0;i<s;i++) {
				reader.deleteDocument(i);
			}
		} catch (Exception e) {
			log.error("Failed to delete index documents: Consider manually removing the directory: " + indexDir.getAbsolutePath());
		}
		log.debug("Finished Deleting documents.");
	}
	
}
