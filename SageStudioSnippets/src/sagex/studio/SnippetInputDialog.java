package sagex.studio;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import sagex.studio.Snippet.FormInput;

public class SnippetInputDialog extends JDialog {
    
    private Map<String , Object> values = new HashMap<String, Object>();
    private boolean cancelled=false;

    public SnippetInputDialog(Frame frame, final Snippet snip, SnippetPluginLoader plugin) {
        super(frame);
        
        setTitle("Inputs for: " + snip.getLabel());
        
        Container c = getContentPane();
        
        c.setLayout(new GridLayout(0,2,5,5));
        
        for (FormInput fi : snip.getInputs()) {
            JLabel l = new JLabel(fi.getLabel());
            c.add(l);
            
            if (fi.isCheckbox()) {
                JCheckBox cb = new JCheckBox();
                cb.setName(fi.getName());
                cb.setSelected(parseBoolean(fi.getValue()));
                values.put(fi.getName(), parseBoolean(fi.getValue()));
                cb.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JCheckBox b = (JCheckBox) e.getSource();
                        values.put(b.getName(), b.isSelected());
                    }
                });
                c.add(cb);
            }
            
            if (fi.isText()) {
                JTextField tb = new JTextField();
                tb.setName(fi.getName());
                tb.setText(fi.getValue());
                values.put(fi.getName(), fi.getValue());
                tb.addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent arg0) {
                    }

                    public void focusLost(FocusEvent arg0) {
                        JTextField t = (JTextField) arg0.getSource();
                        values.put(t.getName(), t.getText());
                    }
                });
                c.add(tb);
            }
        }
        
        JButton ok = new JButton("ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (FormInput fi : snip.getInputs()) {
                    if (fi.isRequired()) {
                        Object  o = values.get(fi.getName());
                        if (o==null || (o instanceof String && ((String)o).trim().length()==0)) {
                            JOptionPane.showMessageDialog(getContentPane(), fi.getLabel() + " is required.");
                            return;
                        }
                    }
                }
                setVisible(false);
            }
        });
        
        JButton cancel = new JButton("cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelled=true;
                setVisible(false);
            }
        });
        
        c.add(ok);
        c.add(cancel);
        
        pack();
    }
    
    private boolean parseBoolean(String value) {
        if (value==null || value.trim().length()==0) return false;
        return Boolean.parseBoolean(value);
    }

    public boolean isCancelled() {
        return cancelled;
    }
    
    public Map<String, Object> getValues() {
        return values;   
    }
}
