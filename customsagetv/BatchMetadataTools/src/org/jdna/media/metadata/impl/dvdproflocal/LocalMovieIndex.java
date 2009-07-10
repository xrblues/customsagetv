package org.jdna.media.metadata.impl.dvdproflocal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataUtil;
import org.w3c.dom.Element;

import sagex.phoenix.configuration.proxy.GroupProxy;

public class LocalMovieIndex implements IDVDProfMovieNodeVisitor {
    private static final Logger    log      = Logger.getLogger(LocalMovieIndex.class);
    private static LocalMovieIndex indexer  = new LocalMovieIndex();

    private IndexReader            reader   = null;
    private IndexWriter            writer   = null;
    private File                   indexDir = null;

    private Searcher               searcher = null;
    private Analyzer               analyzer = new StandardAnalyzer();
    private QueryParser            parser   = new QueryParser("title", analyzer);

    public MetadataConfiguration cfg = GroupProxy.get(MetadataConfiguration.class);
    
    public static LocalMovieIndex getInstance() {
        return indexer;
    }

    public boolean isNew() {
        File ch[] = getIndexDir().listFiles();
        return ch == null || ch.length == 0;
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
        indexDir = getIndexDir();
        if (!indexDir.exists()) {
            log.debug("Creating Lucene Index Dir: " + indexDir.getAbsolutePath());
        }

        log.debug("Opening Lucene Index: " + indexDir.getAbsolutePath());

        reader = IndexReader.open(indexDir);
        searcher = new IndexSearcher(reader);
    }

    public void addMovie(String title, String altTitle, String date, String id) throws Exception {
        log.debug("Indexing Movie: " + title + "; altTitle: "+ altTitle +"; date: " + date + "; id: " + id);
        Document doc = createDocument(title, altTitle, date, id);
        writer.addDocument(doc);
    }

    private File getIndexDir() {
        return indexDir;
    }

    public static Document createDocument(String title, String altTitle, String date, String id) {
        // make a new, empty document
        Document doc = new Document();

        // ISSUE: 24 - remove html tags from the titles
        title=org.jdna.util.StringUtils.removeHtml(title);
        
        // index titles
        doc.add(new Field("title", title, Field.Store.YES, Field.Index.TOKENIZED));

        // store the alt title, if there is one.
        if(!StringUtils.isEmpty(altTitle)) {
            altTitle = org.jdna.util.StringUtils.removeHtml(altTitle);
            doc.add(new Field("alttitle", altTitle, Field.Store.YES, Field.Index.TOKENIZED));
        }

        // Store release date but not index
        doc.add(new Field("release", date, Field.Store.YES, Field.Index.NO));
        doc.add(new Field("id", id, Field.Store.YES, Field.Index.NO));

        // return the document
        return doc;
    }

    public List<IMediaSearchResult> searchTitle(String title) throws Exception {
        if (searcher == null) openIndex();

        Query query = null;
        
        if (cfg.isScoreAlternateTitles()) {
            query = parser.parse(title + " OR alttitle:" + title);
        } else {
            query = parser.parse(title);
        }
        
        Hits hits = searcher.search(query);
        
        int l = hits.length();
        List<IMediaSearchResult> results = new ArrayList<IMediaSearchResult>(l);

        for (int i = 0; i < l; i++) {
            Document d = hits.doc(i);
            String name = d.get("title");
            String altTitle = d.get("alttitle");
            String date = d.get("release");
            String id = d.get("id");
            
            float score1 = MetadataUtil.calculateScore(title, name);
            float score2 = 0.0f;
            if (cfg.isScoreAlternateTitles() && !StringUtils.isEmpty(altTitle)) {
                score2 = MetadataUtil.calculateScore(title, altTitle);
            }
            
            if (!StringUtils.isEmpty(altTitle)) {
                name = name + "(aka "+  altTitle+")";
            }
            results.add(new MediaSearchResult(LocalDVDProfMetaDataProvider.PROVIDER_ID, id, new MetadataID(LocalDVDProfMetaDataProvider.PROVIDER_ID, id), name, date, Math.max(score1, score2)));
        }
        
        return results;
    }

    public void clean() {
        if (isNew()) return;

        log.debug("Deleting All Currently indexed documents.");
        try {
            openIndex();
            int s = reader.numDocs();
            for (int i = 0; i < s; i++) {
                reader.deleteDocument(i);
            }
        } catch (Exception e) {
            log.error("Failed to delete index documents: Consider manually removing the directory: " + indexDir.getAbsolutePath());
        }
        log.debug("Finished Deleting documents.");
    }

    public void setIndexDir(String indexDir2) {
        this.indexDir = new File(indexDir2);
        if (!indexDir.exists()) {
            log.debug("Creating Lucene Index Dir: " + indexDir.getAbsolutePath());
        }
    }

    public void visitMovie(Element el) {
        String id = DVDProfXmlFile.getElementValue(el, "ID");
        String title = DVDProfXmlFile.getElementValue(el, "Title");
        String altTitle = DVDProfXmlFile.getElementValue(el, "OriginalTitle");
        String year = DVDProfXmlFile.getElementValue(el, "ProductionYear");
        try {
            addMovie(title, altTitle, year, id);
        } catch (Exception e) {
            log.error("Can't index movie node: " + el.getTextContent(), e);
        }
    }
}
