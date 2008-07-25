package org.jdna.media;

import java.util.List;


public interface IMediaFolder extends IMediaResource {
	public void setFilter(IMediaResourceFilter filter);
	public IMediaResourceFilter getFilter();
	public void setStackingModel(IMediaStackModel stackingModel);
	public IMediaStackModel getStackingModel();
	public List<IMediaResource> members();
	public boolean contains(String string);
}
