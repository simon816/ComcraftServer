package net.comcraft.src;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.util.Enumeration;

import com.google.minijoe.sys.JsArray;
import com.google.minijoe.sys.JsFunction;
import com.google.minijoe.sys.JsObject;

public class JavaObjectManager extends JsObject {

    private static final int ID_CLASS = 100;

    public JavaObjectManager() {
        super(OBJECT_PROTOTYPE);
        addNative("Class", ID_CLASS, 3);
    }

    @Override
    public Object getObject(String prop) {
        System.out.println(prop);
        return super.getObject(prop);
    }

    @Override
    public void vmGetOperation(JsArray stack, int keyIndex, int valueIndex) {
        System.out.println("vmGetOperation");
        super.vmGetOperation(stack, keyIndex, valueIndex);
    }

    @Override
    public void evalNative(int id, JsArray stack, int sp, int parCount) {
        if (id == ID_CLASS) {
            try {
                createClass(stack, sp);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    private void createClass(JsArray stack, int sp) throws ReflectiveOperationException {
        String className = stack.getString(sp + 2);
        String superClassName = stack.getString(sp + 3);
        JsFunction classBody = (JsFunction) stack.getJsObject(sp + 4);
        Class<?> superClass = Class.forName(superClassName);
        JsArray newStack = new JsArray();
        JsObject context = new JsObject(OBJECT_PROTOTYPE);
        classBody.context = context;
        newStack.setObject(0, context);
        newStack.setObject(1, context);
        newStack.setObject(2, classBody);
        classBody.eval(newStack, 1, 0);
        System.out.println(newStack);
        Enumeration<String> e = context.keys();
        while (e.hasMoreElements()) {
            System.out.print(e.nextElement() + ", ");
        }
        System.out.println();

        for (int i = 2; i < newStack.size(); i++) {
            if (newStack.getObject(i) instanceof JsFunction) {
                JsArray newStack2 = new JsArray();
                newStack2.setObject(0, ModAPI.getInstance());
                newStack2.setObject(1, stack.getObject(sp));
                newStack2.setObject(2, classBody);
                ((JsFunction) newStack.getObject(i)).eval(newStack2, 1, 0);
            }
        }

        Proxy.newProxyInstance(superClass.getClassLoader(), new Class[0], new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
    }
}
