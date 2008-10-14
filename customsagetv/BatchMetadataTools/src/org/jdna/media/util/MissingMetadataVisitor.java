package org.jdna.media.util;

import java.util.ArrayList;
import java.util.List;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IResourceVisitor;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataPersistence;
import org.jdna.util.Strings;

/**
 * Resource Visitor that collects ONLY resources that contain missing metadata.
 * 
 * @author seans
 *
 */
public class MissingMetadataVisitor implements IResourceVisitor {
	private IResourceVisitor visitor;
	private IVideoMetaDataPersistence persistence;
	private List<IMediaResource> missing;
	
	/**
	 * 
	 * @param persistence Where to load the metadata (required)
	 * @param visitor visitor to receive the missing metadata resources (optional)
	 */
	public MissingMetadataVisitor(IVideoMetaDataPersistence persistence, IResourceVisitor visitor) {
		this.persistence = persistence;
		this.visitor = visitor;
		missing = new ArrayList<IMediaResource>();
	}

	public MissingMetadataVisitor(IVideoMetaDataPersistence persistence) {
		this(persistence,null);
	}
	
	public void visit(IMediaResource resource) {
		if (resource instanceof IMediaFile) {
			//System.out.println("Checking: " + resource.getTitle());
			IVideoMetaData md = persistence.loadMetaData((IMediaFile) resource);
			//  )
			//System.out.printf("IsNull: %s %s\n", (md==null), resource.getLocationUri());
			if (md==null || Strings.isEmpty(md.getTitle()) || Strings.isEmpty(md.getThumbnailUrl())) {
				missing.add(resource);
				if (visitor!=null) visitor.visit(resource);
			} // else skip
		} // else skip
	}
	
	public List<IMediaResource> getMissingMetadata() {
		return missing;
	}

}
