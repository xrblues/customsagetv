package org.jdna.persistence;

import java.util.List;

public interface IPersistence {
    public <T extends Object> List<T> loadAll(Class<T> objectType) throws Exception;

    public <T extends Object> T load(Class<T> objectType) throws Exception;

    public <T extends Object> T load(Class<T> objectType, String key) throws Exception;

    public void save(Object obj) throws Exception;

    public void delete(Object cfg) throws Exception;
}
