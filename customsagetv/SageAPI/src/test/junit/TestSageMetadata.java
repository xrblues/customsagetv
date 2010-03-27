package test.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import sagex.api.metadata.ISageCastMember;
import sagex.api.metadata.ISageMetadataALL;
import sagex.api.metadata.SageCastMember;
import sagex.api.metadata.SageMetadata;
import sagex.api.metadata.StringList;
import sagex.api.metadata.StringList.Adapter;
import sagex.util.ILog;
import sagex.util.LogProvider;


public class TestSageMetadata {
    @Test
    public void testStringList() {
        final Map<String,String> map = new HashMap<String, String>();
        map.put("list", "Sean,Carter,,Ethan,Lisa");
        List<String> l = new StringList<String>(new Adapter<String>() {
            public String fromItem(String el) {
                return el;
            }

            public String get() {
                return map.get("list");
            }

            public String getSeparator() {
                return ",";
            }

            public void set(String data) {
                map.put("list",data);
            }

            public String toItem(String data) {
                return data;
            }
        });
        
        assertEquals(4, l.size());
        for (String s: l) {
            System.out.println("Item: " + s);
        }
        
        l.add("Jody");
        assertEquals(5, l.size());
        assertEquals("Sean,Carter,Ethan,Lisa,Jody",map.get("list"));
        
        String s = l.remove(3);
        assertEquals(s, "Lisa");
        assertEquals(4, l.size());
        assertEquals("Sean,Carter,Ethan,Jody",map.get("list"));
    }
    
    @Test
    public void testSageMetadata() {
        LogProvider.useSystemOut();
        ILog log = LogProvider.getLogger(TestSageMetadata.class);
        log.debug("Logger init");
        
        
        ISageMetadataALL all = SageMetadata.create(ISageMetadataALL.class);
        assertNull(all.getAiringTime());

        all.setPartNumber(3);
        assertEquals(3, all.getPartNumber());
        
        assertEquals(false, all.isHDTV());
        all.setHDTV(true);
        assertEquals(true, all.isHDTV());
        
        List<ISageCastMember> list  = all.getActors();
        assertNotNull(list);
        assertEquals(0, list.size());
        all.getActors().add(new SageCastMember("Sean", "Programmer"));
        all.getActors().add(new SageCastMember("Ethan", "QA"));
        list = all.getActors();
        assertEquals(2, list.size());

        all.setOriginalAirDate(Calendar.getInstance().getTime());
        assertNotNull(all.getOriginalAirDate());
        
        Properties props = SageMetadata.createProperties(all);
        assertEquals("3", props.getProperty("PartNumber"));
        
        System.out.println("Properties....");
        props.list(System.out);
    }
}
