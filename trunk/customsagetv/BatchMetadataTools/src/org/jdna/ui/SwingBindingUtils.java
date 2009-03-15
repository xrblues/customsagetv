package org.jdna.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

public class SwingBindingUtils {
    public static JCheckBox createCheckBox(String label, final Object bindObject, final String bindSetMethod, final String bindGetMethod) {
        JCheckBox c = null;
        try {
            Class cl = bindObject.getClass();
            final Method setMethod = cl.getMethod(bindSetMethod, boolean.class);
            final Method getMethod = cl.getMethod(bindGetMethod, null);

            c = new JCheckBox(label);
            c.setSelected((Boolean) getMethod.invoke(bindObject, null));

            c.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    JCheckBox cb = (JCheckBox) evt.getSource();
                    System.out.println("Property Changed: " + cb.getText() + "; " + cb.isSelected());
                    try {
                        setMethod.invoke(bindObject, cb.isSelected());
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Invalid Binding!", e);
        }

        return c;
    }

    public static JTextField createTextBox(final Object bindObject, final Object key) {
        Class cl = bindObject.getClass();
        JTextField t = new JTextField();
        try {
            final Method setMethod = cl.getMethod("set", key.getClass(), Object.class);
            final Method getMethod = cl.getMethod("get", key.getClass());
            t.setText((String)getMethod.invoke(bindObject, key));
            t.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    try {
                        setMethod.invoke(bindObject, key, ((JTextField)e.getSource()).getText());
                    } catch (IllegalArgumentException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Inavalid Binding!", e);
        }
        return t;
    }

}
