package org.jdna.media.metadata.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

public class StubMetadataProvider implements IMediaMetadataProvider {
    private IProviderInfo info = null;

    private Map<IMetadataSearchResult, IMediaMetadata> metadataResultMap = new HashMap<IMetadataSearchResult, IMediaMetadata>();
    private Map<String, IMediaMetadata> metadataIdMap = new HashMap<String, IMediaMetadata>();
    private MediaType[] types = new MediaType[] {MediaType.MOVIE};
    private List<IMetadataSearchResult> results = new LinkedList<IMetadataSearchResult>();
    
    public StubMetadataProvider() {
        this("stub", "Stub Provider");
    }

    public StubMetadataProvider(String id, String name) {
        info = new ProviderInfo(id, name, name, null);
    }
    
    public IProviderInfo getInfo() {
        return info;
    }
    
    public void addMetadata(IMetadataSearchResult result, IMediaMetadata md) {
        results.add(result);
        
        metadataResultMap.put(result, md);
        addMetadata(result.getId(), md);
    }
    
    public void addMetadata(String id, IMediaMetadata md) {
        System.out.println("Adding MetadataId: " + id);
        metadataIdMap.put(id, md);
    }
    
    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        IMediaMetadata md = metadataResultMap.get(result);
        if (md==null) {
            System.out.println("No Metadata for: " + result + "; consider using addMetadata(result, md); Will try by ID.");
            md = metadataIdMap.get(result.getId());
        }
        return md;
    }

    public MediaType[] getSupportedSearchTypes() {
        return types;
    }

    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception {
        System.out.println("StubSearchProvider: searching: " + query);
        if (results==null || results.size()==0) {
            System.out.println("Consider using addMetadata(result, metadata) to add results.");
            return null;
        }
        return results;
    }

    public void setSupportedSearchTypes(MediaType[] types) {
        this.types = types;
    }
    
    public void reset() {
        metadataIdMap.clear();
        metadataResultMap.clear();
        results.clear();
    }
}
