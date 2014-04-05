package net.comcraft.src;

import java.util.Hashtable;
import java.util.Vector;

import com.google.minijoe.sys.JsArray;
import com.google.minijoe.sys.JsException;
import com.google.minijoe.sys.JsFunction;
import com.google.minijoe.sys.JsObject;

public class EventHandler {

    private Hashtable<String, Vector<Object>[]> events;

    public EventHandler(String[] eventnames) {
        this();
        for (int i = 0; i < eventnames.length; i++) {
            addEventName(eventnames[i]);
        }
    }

    public EventHandler() {
        events = new Hashtable<String, Vector<Object>[]>();
    }

    protected void addEventName(String name) {
        @SuppressWarnings("unchecked")
        Vector<Object>[] v = new Vector[2];
        v[0] = new Vector<Object>();
        v[1] = new Vector<Object>();
        events.put(name, v);
    }

    public boolean hasEvent(String name) {
        return events.containsKey(name);
    }

    public void runEvent(String name, Object[] params) {
        runEvent(name, null, params);
    }

    public void runEvent(String name, JsObject thisPtr, Object[] params) {
        if (!hasEvent(name))
            return;
        if (params == null) {
            params = new Object[0];
        }
        Vector<Object>[] event = events.get(name);
        if (event[0].isEmpty()) {
            return;
        }
        Vector<?> e = event[0];
        JsArray stack = new JsArray();
        for (int ce = 0; ce < e.size(); ce++) {
            JsFunction fn = (JsFunction) e.elementAt(ce);
            stack.setObject(0, thisPtr != null ? thisPtr : fn); // context ('this' variable)
            stack.setObject(1, fn); // Function
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) {
                    stack.setInt(i + 2, ((Integer) params[i]).intValue());
                } else if (params[i] instanceof Boolean) {
                    stack.setBoolean(i + 2, ((Boolean) params[i]).booleanValue());
                } else {
                    stack.setObject(i + 2, params[i]);
                }
            }
            try {
                fn.eval(stack, 0, params.length);
                event[1].setElementAt(stack.getObject(0), ce);
            } catch (Exception e1) {
                System.err.println(e1.getMessage());
                e1.printStackTrace();
            }
        }
    }

    public void runEvent(String name) {
        runEvent(name, null);
    }

    public void bindEvent(String name, JsFunction function) {
        if (name == null || function == null) {
            return;
        }
        if (!events.containsKey(name)) {
            throw new JsException("Unknown Event key " + name);
        }
        Vector<Object>[] e = events.get(name);
        e[0].addElement(function);
        e[1].addElement(null);
    }

    public boolean bindEventOnce(String name, JsFunction function) {
        if (!hasEvent(name))
            return false;
        Vector<?> fnlist = events.get(name)[0];
        for (int i = 0; i < fnlist.size(); i++) {
            if (((JsFunction) fnlist.elementAt(i)).equals(function)) {
                return false;
            }
        }
        bindEvent(name, function);
        return true;
    }

    public void setEvent(String name, JsFunction function) {
        if (name == null || function == null) {
            return;
        }
        if (!events.containsKey(name)) {
            throw new JsException("Unknown Event key " + name);
        }
        Vector<Object>[] e = events.get(name);
        e[0].removeAllElements();
        e[1].removeAllElements();
        e[0].addElement(function);
        e[1].addElement(null);
    }

    public Object getLastReturn(String name) {
        return events.get(name)[1].lastElement();
    }

    public Object getLastSuccess(String name) {
        if (!hasEvent(name))
            return null;
        Vector<?> event = events.get(name)[1];
        Object r = null;
        for (int i = 0; i < event.size(); i++) {
            Object t = event.elementAt(i);
            if (t != null) {
                r = t;
            }
        }
        return r;
    }

    public Object getFirstSuccess(String name) {
        Vector<?> event = events.get(name)[1];
        for (int i = 0; i < event.size(); i++) {
            Object r = event.elementAt(i);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    public Object[] getSucesses(String name) {
        Vector<?> event = events.get(name)[1];
        Vector<Object> v = new Vector<Object>();
        for (int i = 0; i < event.size(); i++) {
            Object r = event.elementAt(i);
            if (r != null) {
                v.addElement(r);
            }
        }
        Object[] arr = new Object[v.size()];
        v.copyInto(arr);
        return arr;
    }
}
