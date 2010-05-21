package org.jdna.process;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;

import sagex.phoenix.vfs.IMediaFile;

public class MetadataItem {
    private IMediaFile file;
    private IMediaMetadata metadata;
    private SearchQuery query;
    public MetadataItem(IMediaFile file, SearchQuery query, PersistenceOptions options, IMediaMetadataProvider provider, IMediaMetadataPersistence persistence, IMediaMetadata metadata) {
        super();
        this.file = file;
        this.query = query;
        this.options = options;
        this.provider = provider;
        this.persistence = persistence;
        this.metadata = metadata;
    }
    private PersistenceOptions options;
    private IMediaMetadataProvider provider;
    private IMediaMetadataPersistence persistence;
    /**
     * @return the file
     */
    public IMediaFile getFile() {
        return file;
    }
    /**
     * @return the metadata
     */
    public IMediaMetadata getMetadata() {
        return metadata;
    }
    /**
     * @return the query
     */
    public SearchQuery getQuery() {
        return query;
    }
    /**
     * @return the options
     */
    public PersistenceOptions getOptions() {
        return options;
    }
    /**
     * @return the provider
     */
    public IMediaMetadataProvider getProvider() {
        return provider;
    }
    /**
     * @return the persistence
     */
    public IMediaMetadataPersistence getPersistence() {
        return persistence;
    }
}
