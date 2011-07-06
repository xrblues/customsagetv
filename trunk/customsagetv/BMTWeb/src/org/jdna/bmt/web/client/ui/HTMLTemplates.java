package org.jdna.bmt.web.client.ui;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface HTMLTemplates extends SafeHtmlTemplates {
	@Template("<div><b>{0}</b><br>{1} <em>at line {2} and column {3}</em></div>")
	public SafeHtml createConfigError(String name, String msg, int line, int col);

	@Template("<a class=\"MetadataPunchout\" href=\"{1}\" target=\"{2}\">{0}</a>")
	public SafeHtml createMetadataPunchout(String name, String url, String targetId);

	@Template("<a class=\"MetadataPunchout\" href=\"http://www.imdb.com/title/{1}/\" target=\"_imdb\">{0}</a>")
	public SafeHtml createIMDBPunchout(String name, String id);

	@Template("<a class=\"MetadataPunchout\" href=\"http://www.themoviedb.org/movie/{1}\" target=\"_tmdb\">{0}</a>")
	public SafeHtml createTMDBPunchout(String name, String id);

	@Template("<a class=\"MetadataPunchout\" href=\"http://thetvdb.com/?tab=series&id={1}\" target=\"_tmdb\">{0}</a>")
	public SafeHtml createTVDBPunchout(String name, String id);
}
