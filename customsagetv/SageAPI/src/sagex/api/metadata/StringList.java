package sagex.api.metadata;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * A list facade that backed by some fake "string" list of items, ie, a csv string.  This shows the backing
 * string list as a java list and all changes the java list and make to backing string list.  This list
 * requires and an {@link Adapter} be used to adapt this java list to the backing string csv list.
 * 
 * The primary use for this list is to provide an automated adapter between a SageTV metadata list, ie Actors, and
 * a Java List.
 * 
 * @author seans
 */
public class StringList<E> extends AbstractList<E> {
    /**
     * Adapts the data to/from a string area
     * @author seans
     *
     * @param <E>
     */
    public interface Adapter<E> {
        /**
         * The separator for the data, ie, colon, semi-colon, comma, etc.
         * @return
         */
        public String getSeparator();
        /**
         * Get the data
         * @return
         */
        public String get();
        /**
         * Set the data
         * @param data
         */
        public void set(String data);
        /**
         * Convert the data to a Typed item for the list 
         * @param data
         * @return
         */
        public E toItem(String data);
        /**
         * Convert the typed item to a string 
         * @param el
         * @return
         */
        public String fromItem(E el); 
    }
    private List<E> list = new ArrayList<E>();
    private Adapter<E> adapter;
    
    public StringList(Adapter<E> adapter) {
        this.adapter=adapter;
        // init the list
        String s = adapter.get();
        if (s==null||s.trim().length()==0) return;
        
        String items[] = s.split(adapter.getSeparator());
        if (items==null||items.length==0) return;
        
        for (String i: items) {
            if (i!=null && i.trim().length()>0) {
                list.add(adapter.toItem(i));
            }
        }
    }

    private void notifyAdapter() {
        // basically takes the current state of the list and sends it to the adapter
        StringBuffer sb = new StringBuffer();
        for (int i=0;i<list.size();i++) {
            if (i>0) {
                sb.append(adapter.getSeparator());
            }
            sb.append(adapter.fromItem(list.get(i)));
        }
        adapter.set(sb.toString());
    }

    @Override
    public E get(int arg0) {
        return list.get(arg0);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public void add(int index, E element) {
        list.add(index, element);
        notifyAdapter();
    }

    @Override
    public E remove(int index) {
        E o = list.remove(index);
        notifyAdapter();
        return o;
    }

    @Override
    public E set(int index, E element) {
        E o = list.set(index, element);
        notifyAdapter();
        return o;
    }
}
