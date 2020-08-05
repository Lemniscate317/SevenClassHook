package com.l.sevenclasshook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        //System.loadLibrary("sandhook-native");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        try {
//            GetClassLoaderClasslist(getClassLoader());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public void GetClassLoaderClasslist(ClassLoader classLoader) throws ClassNotFoundException {

        Object pathListObj = getObjectField(classLoader, "pathList");
        //private final Element[] dexElements;
        Object[] dexElementsObj = (Object[]) getObjectField(pathListObj, "dexElements");
        for (Object i : dexElementsObj) {
            //private final DexFile dexFile;
            Object dexFileObj = getObjectField(i, "dexFile");
            //private Object mCookie;
            Object mCookieObj = getObjectField(dexFileObj, "mCookie");
            //private static native String[] getClassNameList(Object cookie);
            //    public static java.lang.Object callStaticMethod(java.lang.Class<?> clazz, java.lang.String methodName, java.lang.Object... args) { /* compiled code */ }
            Class DexFileClass = classLoader.loadClass("dalvik.system.DexFile");

            String[] classlist = (String[]) invokeStaticMethod(DexFileClass.getName(), "getClassNameList", new Class[]{mCookieObj.getClass()}, new Object[]{mCookieObj});
            for (String classname : classlist) {
//                XposedBridge.log(dexFileObj + "---" + classname);
                try {
                    classLoader.loadClass(classname);
                    Log.e("Hook1", "loadclass:" + classname);
                } catch (ClassNotFoundException e) {
                    Log.e("Hook1", Log.getStackTraceString(e));
                }
            }
        }
//        XposedBridge.log("end dealwith classloader:" + classLoader);

//        Object pathListObj = XposedHelpers.getObjectField(classLoader, "pathList");
//        //private final Element[] dexElements;
//        Object[] dexElementsObj = (Object[]) XposedHelpers.getObjectField(pathListObj, "dexElements");
//        for (Object i : dexElementsObj) {
//            //private final DexFile dexFile;
//            Object dexFileObj = XposedHelpers.getObjectField(i, "dexFile");
//            //private Object mCookie;
//            Object mCookieObj = XposedHelpers.getObjectField(dexFileObj, "mCookie");
//            //private static native String[] getClassNameList(Object cookie);
//            //    public static java.lang.Object callStaticMethod(java.lang.Class<?> clazz, java.lang.String methodName, java.lang.Object... args) { /* compiled code */ }
//            Class DexFileClass = XposedHelpers.findClass("dalvik.system.DexFile", classLoader);
//
//            String[] classlist = (String[]) invokeStaticMethod(DexFileClass.getName(), "getClassNameList", mCookieObj);
//            for (String classname : classlist) {
////                XposedBridge.log(dexFileObj + "---" + classname);
//                try {
//                    classLoader.loadClass(classname);
//                    Log.e("Hook1", "loadclass:" + classname);
//                } catch (ClassNotFoundException e) {
//                    Log.e("Hook1", Log.getStackTraceString(e));
//                }
//            }
//        }
        //XposedBridge.log("end dealwith classloader:" + classLoader);

    }

    public static Object getObjectField(Object object, String fieldName) {
        Class clazz = object.getClass();
        while (!clazz.getName().equals(Object.class.getName())) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
        }

        return null;
    }

    public static Field getClassField(ClassLoader classloader, String class_name,
                                      String filedName) {

        try {
            Class obj_class = classloader.loadClass(class_name);//Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Object getClassFieldObject(ClassLoader classloader, String class_name, Object obj,
                                             String filedName) {

        try {
            Class obj_class = classloader.loadClass(class_name);//Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            Object result = null;
            result = field.get(obj);
            return result;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Object invokeStaticMethod(String class_name,
                                            String method_name, Class[] pareTyple, Object[] pareVaules) {

        try {
            Class obj_class = Class.forName(class_name);
            Method[] methods = obj_class.getMethods();
            Method method = null;
            for (Method m : methods) {
                if (m.getName().equals(method_name)) {
                    method = m;
                    break;
                }
            }
            if (method == null) {
                for (Method m : obj_class.getDeclaredMethods()) {
                    if (m.getName().equals(method_name)) {
                        method = m;
                        method.setAccessible(true);
                        break;
                    }
                }
            }
            return method.invoke(null, pareVaules);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Object getFieldOjbect(String class_name, Object obj,
                                        String filedName) {
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static ClassLoader getClassloader() {
        ClassLoader resultClassloader = null;
        Object currentActivityThread = invokeStaticMethod(
                "android.app.ActivityThread", "currentActivityThread",
                new Class[]{}, new Object[]{});
        Object mBoundApplication = getFieldOjbect(
                "android.app.ActivityThread", currentActivityThread,
                "mBoundApplication");
        Application mInitialApplication = (Application) getFieldOjbect("android.app.ActivityThread",
                currentActivityThread, "mInitialApplication");
        Object loadedApkInfo = getFieldOjbect(
                "android.app.ActivityThread$AppBindData",
                mBoundApplication, "info");
        Application mApplication = (Application) getFieldOjbect("android.app.LoadedApk", loadedApkInfo, "mApplication");
        resultClassloader = mApplication.getClassLoader();
        return resultClassloader;
    }
}
