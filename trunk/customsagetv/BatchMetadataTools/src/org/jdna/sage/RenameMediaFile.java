package org.jdna.sage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.Format;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;

import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.util.PathUtils;

/**
 * TODO: Re-work rename so that it becomes a part of the PersistenceOptions, ie, if rename is enabled, then the mask
 * will be in the persistence options 
 *
 */
public class RenameMediaFile implements IMediaMetadataPersistence {
    private static final Logger log = Logger.getLogger(RenameMediaFile.class);

    /**
     * Create a <code>Map</code> of variable names to values based on the file data and metadata.
     * 
     * @param md The metadata used to form variables
     * @param mediaFile The file used to form variables
     * @return The <code>Map</code>
     */
    private static Map<String, String> createVariableMap(IMediaMetadata md, IMediaResource mediaFile) {
		Map<String, String> retVal = new HashMap<String, String>();
		
		retVal.put("FileExtension", PathUtils.getExtension(mediaFile));
		retVal.put("FileBasename", PathUtils.getBasename(mediaFile));
		retVal.put("FileName", PathUtils.getName(mediaFile));
		retVal.put("FileTitle", mediaFile.getTitle());

		//loop over all metadata allow replacement based on those
		for(MetadataKey key : MetadataKey.values()){
			retVal.put(key.getId(), md.getString(key));
		}
		
		return retVal;
	}
	
	/**
	 * Given a string that may contain variables, replace the variables with their (optionally formatted) 
	 * values.
	 * The variables may be in one of two forms:
	 * <ol>
	 * <li>${name}
	 * <li>${name:class:format}
	 * </ol>
	 * In the first form, the variable named <i>name</i> is replaced with its value.
	 * In the second form, the variable named <i>name</i> is replaced with its value after that value
	 * has been formatted using the subclass of <code>java.text.Format</code> named in <i>class</i> and
	 * the format string (passed to the formatter's constructor) named in <i>format</i>.
	 * 
	 * As an example of the second form, consider the variable <i>SeasonNumber</i>.  To replace this variable
	 * with its integer value, formatted to always contain 2 digits (e.g. 1 is formatted to 01) use the 
	 * variable <code>${SeasonNumber:java.text.DecimalFormat:00}</code>
	 * 
	 * @param string The <code>String</code> whose variables are being replaced 
	 * @param replacements A <code>Map<code> whose keys are possible variable names and whose values are the
	 * <code>String</code>s with which the variable should be replaced
	 * @return The given <code>String</code> with all possible variable substitutions made
	 * @throws Exception
	 */
	private static String replaceVariables(String string, Map<String,String> replacements) {
		String retVal = new String(string);
		Pattern pattern = Pattern.compile(".*?\\$\\{(\\w*)(?:\\:([a-zA-Z_0-9.]*)\\:([^}]*))?\\}.*?");
		Matcher matcher = pattern.matcher(retVal);

		while(matcher.matches()) {
			String key = matcher.group(1);
			String formatClassName = matcher.group(2);
			String format = matcher.group(3);
			
			String val = replacements.get(key);
			if(val != null) {
				StringBuffer replaceVal = new StringBuffer();
				replaceVal.append("\\$\\{").append(key);
				
				if(formatClassName != null && format != null) {
					replaceVal.append(":").append(formatClassName);
					replaceVal.append(":").append(format);
					
					Format formatter = getFormat(formatClassName, format);
					if(formatter != null) {
						try {
							Object o = formatter.parseObject(val);
							val = formatter.format(o);
						} catch(ParseException pe) {
							log.error("Could not format value because it was not parsable by the configured formatter." +
									" Value: " + val + " Formatter: " + formatClassName + " Format: " + format, pe);
						}
					}
				}

				replaceVal.append("}");
				
 				retVal = retVal.replaceAll(replaceVal.toString(), val);
			}
			matcher = pattern.matcher(retVal);
			
		}

		return retVal;
	}

	/**
	 * Given a class name and a format, create and instance of the class, which
	 * must be a concrete subclass of <code>java.text.Format</code>, and 
	 * initialize it with the format.
	 * 
	 * @param formatClassName The name of a class, a subclass of <code>java.text.Format</code>
	 * @param format The format to be used
	 * @return An initialized subclass of <code>java.text.Format</code>
	 */
	private static Format getFormat(String formatClassName, String format) {
		Format formatter = null;
		
		try {
			Class<?> c = Class.forName(formatClassName);
			if(Format.class.isAssignableFrom(c)) {
				Class<? extends Format> formatClass = c.asSubclass(Format.class);
				Constructor<? extends Format> constructor = formatClass.getConstructor(String.class);
				formatter = constructor.newInstance(format);
			}
		} catch(ClassNotFoundException cnfe) {
			log.error("Could not instantiate Format Object because the class name is invalid: " + formatClassName, cnfe);
		} catch(NoSuchMethodException nsme) {
			log.error("Invalid Format Class.  Instance of java.text.Format doesn't have a (String) constructor.", nsme);
		} catch(InvocationTargetException ite) {
			log.error("Could not instantiate format Object.", ite);
		} catch(IllegalAccessException iae) {
			log.error("Could not instantiate format Object.", iae);
		} catch(InstantiationException ie) {
			log.error("Could not instantiate format Object.", ie);
		}
		
		return formatter;
	}
    
    public String getDescription() {
        return "Renames the media file based on metadata using a configured pattern";
    }

    public String getId() {
        return "renameFile";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        return new MediaMetadata();
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        if (mediaFile!=null && mediaFile.exists()) {
        	String patternString = options.getFileRenamePattern();
        	if(patternString != null) {
        		log.debug("Renaming: " + mediaFile);

        		patternString = replaceVariables(patternString, createVariableMap(md, mediaFile));
        		
        		File newFile = new File(patternString);
        		//We may need to create some directories
        		File newParent = newFile.getParentFile();
        		if(!newParent.exists()) {
        			if(!newParent.mkdirs()) {
        				log.error("Failed to created directory: " + newParent.getPath());
        			}
        		}
        		
        		File files[] = ((IMediaFile)mediaFile).getFiles();
        		if (files==null || files.length!=1) {
        		    log.warn("Cannot rename mediafile; either too many files, or no files are in its collection.");
        		    return;
        		}
        		
        		if(!files[0].renameTo(newFile)) {
        			log.error("Failed to rename file to: " + patternString);
        		}
        	}
        }
    }
}