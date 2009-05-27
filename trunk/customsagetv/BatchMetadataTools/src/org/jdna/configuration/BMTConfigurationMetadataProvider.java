package org.jdna.configuration;

import java.io.IOException;

import org.jdna.media.MediaConfiguration;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.impl.dvdproflocal.DVDProfilerLocalConfiguration;
import org.jdna.media.metadata.impl.imdb.IMDBConfiguration;
import org.jdna.media.metadata.impl.mymovies.MyMoviesConfiguration;
import org.jdna.media.metadata.impl.sage.SageMetadataConfiguration;
import org.jdna.metadataupdater.MetadataUpdaterConfiguration;
import org.jdna.url.UrlConfiguration;

import sagex.phoenix.configuration.Field;
import sagex.phoenix.configuration.Group;
import sagex.phoenix.configuration.IConfigurationMetadata;
import sagex.phoenix.menu.Variable;

public class BMTConfigurationMetadataProvider implements IConfigurationMetadata {
    public Group[] load() throws IOException {
        Group root = new Group();
        root.setElementType(Group.APPLICATION);
        root.setLabel("Metadata Tools (BMT)");
        root.setId("bmt");
        root.setDescription("Configuration for Batch Metadata Tools");
        
        Class[] classes = new Class[] {
           MetadataUpdaterConfiguration.class,
           MetadataConfiguration.class,
           MediaConfiguration.class,
           UrlConfiguration.class,
           DVDProfilerLocalConfiguration.class,
           IMDBConfiguration.class,
           MyMoviesConfiguration.class,
           SageMetadataConfiguration.class,
        };
        
        for (Class cl : classes) {
            addGroup(root, cl);
        }
        
        return new Group[] {root};
    }

    private void addGroup(Group root, Class cl) {
        if (! GroupProxy.class.isAssignableFrom(cl)) {
            throw new RuntimeException("Not Configured! Class should be a SubClass of GroupProxy: " + cl.getName());
        }
        
        org.jdna.configuration.Group g = (org.jdna.configuration.Group) cl.getAnnotation(org.jdna.configuration.Group.class);
        if (g==null) {
            throw new RuntimeException("Missing @Group annotation for " + cl.getName());
        }
        
        if (!g.path().startsWith("bmt/")) {
            throw new RuntimeException("BMT Configuration Groups must start with 'bmt/'; This does not: " + g.path() + " for class: " + cl.getName());
        }
        
        GroupProxy groupProxy = null;
        try {
            groupProxy=(GroupProxy) cl.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to create new instance of " + cl.getName(), e);
        }
        
        // define the group metadata
        Group gr = new Group(g.path());
        gr.setLabel(g.label());
        gr.setDescription(g.description());

        // define the group field metadata
        for (java.lang.reflect.Field f : cl.getDeclaredFields()) {
            if (!FieldProxy.class.isAssignableFrom(f.getType())) {
                throw new RuntimeException("Field: " + f.getName() + " in ProxyGroup: " + cl.getName() + " is not a FieldProxy class!");
            }
            
            org.jdna.configuration.Field annFld = f.getAnnotation(org.jdna.configuration.Field.class);
            if (annFld==null) {
                throw new RuntimeException("Missing @Field for field: " + f.getName() + " in GroupProxy: " + cl.getName());
            }
            
            String groupId = gr.getId();
            Field fld = new Field();
            String id = null;
            if (!annFld.fullKey().equals(org.jdna.configuration.Field.USE_PARENT_GROUP)) {
                id = annFld.fullKey();
            } else {
                if (annFld.name().equals(annFld.USE_FIELD_NAME)) {
                    id = groupId + "/" + f.getName();
                } else {
                    id = groupId + "/" + annFld.name();
                }
            }
            fld.setId(id);
            fld.setDescription(annFld.description());
            fld.setLabel(annFld.label());
            fld.setType(getType(f));
            try {
                f.setAccessible(true);
                fld.setDefaultValue(((FieldProxy)f.get(groupProxy)).getDefaultValueAsString());
            } catch (Throwable t) {
                throw new RuntimeException("Failed to get default value for: " + f.getName() + " in ProxyGroup: " + cl.getName(), t);
            }
            fld.setIsVisible(new Variable<Boolean>(!annFld.hidden()));
            gr.addElement(fld);
        }
        
        if (gr.getChildren().length==0) {
            throw new RuntimeException("Didn't add any Children to ProxyGroup for class: " + cl.getName());
        }
        
        root.addElement(gr);
    }

    private String getType(java.lang.reflect.Field f) {
        String s = f.toGenericString();
        if (s.contains(String.class.getName())) {
            return "string";
        } else if (s.contains(Integer.class.getName())) {
            return "int";
        } else if (s.contains(Long.class.getName())) {
            return "long";
        } else if (s.contains(Float.class.getName())) {
            return "float";
        } else if (s.contains(Boolean.class.getName())) {
            return "boolean";
        } else {
            return "string";
        }
    }

    public void save() throws IOException {
    }
}
