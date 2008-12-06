package org.jdna.media.util;

import java.util.ArrayList;
import java.util.List;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.util.Strings;

/**
 * Resource Visitor that collects ONLY resources that contain missing metadata.
 * 
 * @author seans
 *
 */
public class MissingMetadataVisitor implements IMediaResourceVisitor {
	private IMediaResourceVisitor visitor;
	private List<IMediaResource> missing;
	
	/**
	 * 
	 * @param persistence Where to load the metadata (required)
	 * @param visitor visitor to receive the missing metadata resources (optional)
	 */
	public MissingMetadataVisitor(IMediaResourceVisitor visitor) {
		this.visitor = visitor;
		missing = new ArrayList<IMediaResource>();
	}

	public MissingMetadataVisitor() {
		this(null);
	}
	
	public void visit(IMediaResource resource) {
		if (isMissingMetadata(resource)) {
			missing.add(resource);
			if (visitor!=null) visitor.visit(resource);
		}
	}
	
	public List<IMediaResource> getMissingMetadata() {
		return missing;
	}

	public static boolean isMissingMetadata(IMediaResource resource) {
		if (resource.getType() == IMediaFile.TYPE_FILE) {
			IMediaMetadata md = resource.getMetadata();
			if (md==null || Strings.isEmpty(md.getTitle()) || Strings.isEmpty(md.getThumbnailUrl())) {
				return true;
			} // else skip
		} // else skip
		return false;
	}
	
}
