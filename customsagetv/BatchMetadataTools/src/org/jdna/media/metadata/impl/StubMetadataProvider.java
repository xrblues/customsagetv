package org.jdna.media.metadata.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;

public class StubMetadataProvider implements IMediaMetadataProvider {
    private IProviderInfo info = null;

    private Map<IMediaSearchResult, IMediaMetadata> metadataResultMap = new HashMap<IMediaSearchResult, IMediaMetadata>();
    private Map<MetadataID, IMediaMetadata> metadataIdMap = new HashMap<MetadataID, IMediaMetadata>();
    private Map<String, IMediaMetadata> metadataUrlMap = new HashMap<String, IMediaMetadata>();
    private SearchQuery.Type[] types = new SearchQuery.Type[] {SearchQuery.Type.MOVIE};
    private List<IMediaSearchResult> results = new LinkedList<IMediaSearchResult>();
    
    public StubMetadataProvider() {
        this("stub", "Stub Provider");
    }

    public StubMetadataProvider(String id, String name) {
        info = new ProviderInfo(id, name, name, null);
    }
    
    public IProviderInfo getInfo() {
        return info;
    }
    
    public void addMetadata(IMediaSearchResult result, IMediaMetadata md) {
        results.add(result);
        
        metadataResultMap.put(result, md);
        addMetadata(result.getMetadataId(), md);
        addMetadata(result.getUrl(), md);
    }
    
    public void addMetadata(MetadataID id, IMediaMetadata md) {
        System.out.println("Adding MetadataId: " + id);
        metadataIdMap.put(id, md);
    }
    
    public void addMetadata(String url, IMediaMetadata md) {
        System.out.println("Adding Metadata Url: " + url);
        metadataUrlMap.put(url, md);
    }
    
    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
        IMediaMetadata md = metadataResultMap.get(result);
        if (md==null) {
            System.out.println("No Metadata for: " + result + "; consider using addMetadata(result, md); Will try by ID.");
            md = getMetaDataByUrl(getUrlForId(result.getMetadataId()));
            if (md==null) {
                System.out.println("Trying by url");
                md = getMetaDataByUrl(result.getUrl());
            }
            if (md==null) {
                return null;
            }
        }
        return md;
    }

    public String getUrlForId(MetadataID id) throws Exception {
        IMediaMetadata md = metadataIdMap.get(id);
        if (md==null) {
            System.out.println("No Metadata for: " + id + "; consider using addMetadata(metadataid, md)");
            return null;
        }
        return MetadataAPI.getProviderDataUrl(md);
    }

    public IMediaMetadata getMetaDataByUrl(String url) throws Exception {
        IMediaMetadata md = metadataUrlMap.get(url);
        if (md==null) {
            System.out.println("No Metadata for: " + url + "; consider using addMetadata(url, md)");
            return null;
        }
        return md;
    }

    public Type[] getSupportedSearchTypes() {
        return types;
    }

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        System.out.println("StubSearchProvider: searching: " + query);
        if (results==null || results.size()==0) {
            System.out.println("Consider using addMetadata(result, metadata) to add results.");
            return null;
        }
        return results;
    }

    public void setSupportedSearchTypes(SearchQuery.Type[] types) {
        this.types = types;
    }
    
    public void reset() {
        metadataIdMap.clear();
        metadataResultMap.clear();
        metadataUrlMap.clear();
        results.clear();
    }
}
