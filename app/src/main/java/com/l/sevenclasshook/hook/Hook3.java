package com.l.sevenclasshook.hook;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import de.robv.android.xcustom.IXcustomHookLoadPackage;
import de.robv.android.xcustom.XC_MethodHook;
import de.robv.android.xcustom.XcustomBridge;
import de.robv.android.xcustom.XcustomHelpers;
import de.robv.android.xcustom.callbacks.XC_LoadPackage;


public class Hook3 implements IXcustomHookLoadPackage {
    private Context mContext;
    private List<ClassLoader> mClassLoaders = new ArrayList<>();


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XcustomBridge.log("test can log");
        if (lpparam.packageName.equals("com.sup.android.superb") && lpparam.isFirstApplication) {
            XcustomBridge.log(lpparam.packageName + "  enter");


            XcustomBridge.hookAllConstructors(DexClassLoader.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    final ClassLoader classLoader = (ClassLoader) param.thisObject;
                    XcustomBridge.log("DexClassLoader:" + classLoader.toString());

                    //testSomeClassExist(classLoader);

                    mClassLoaders.add(classLoader);

                }
            });

            XcustomBridge.hookAllConstructors(PathClassLoader.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    final ClassLoader classLoader = (ClassLoader) param.thisObject;
                    XcustomBridge.log("PathClassLoader:" + classLoader.toString());

                    //testSomeClassExist(classLoader);

                    mClassLoaders.add(classLoader);
                }
            });


            XcustomHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    XcustomBridge.log("attach after");

                    mContext = (Context) param.args[0];


                    XcustomHelpers.callMethod(Runtime.getRuntime(), "doLoad", "/system/lib/libnative-lib.so", mContext.getClassLoader());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(30 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Log.e("hook1", Log.getStackTraceString(e));
                            }


                            for (int i = 0; i < mClassLoaders.size(); i++) {
                                ClassLoader classLoader = mClassLoaders.get(i);
                                TestClassloader(classLoader);
                            }

                            fart();
                        }
                    }).start();

                    //GetClassLoaderClasslist(mContext.getClassLoader());

                }
            });
        }
    }

    public ClassLoader getClassloader() {
        ClassLoader resultClassloader = null;
        try {
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
            XcustomBridge.log("Applicatoin->" + mApplication);
            resultClassloader = mApplication.getClassLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultClassloader;
    }

    public Field getClassField(ClassLoader classloader, String class_name,
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

    public Object getClassFieldObject(ClassLoader classloader, String class_name, Object obj,
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

    public Object invokeStaticMethod(String class_name,
                                     String method_name, Class[] pareTyple, Object[] pareVaules) {

        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getMethod(method_name, pareTyple);
            return method.invoke(null, pareVaules);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public Object getFieldOjbect(String class_name, Object obj,
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

    public void fart() {
        ClassLoader appClassloader = getClassloader();
        if (appClassloader == null) {
            return;
        }
        ClassLoader tmpClassloader = appClassloader;
        ClassLoader parentClassloader = appClassloader.getParent();


        if (appClassloader.toString().indexOf("java.lang.BootClassLoader") == -1) {
//            testSomeClassExist(appClassloader);
            TestClassloader(appClassloader);
        }
        while (parentClassloader != null) {
            if (parentClassloader.toString().indexOf("java.lang.BootClassLoader") == -1) {
//                testSomeClassExist(parentClassloader);
                TestClassloader(parentClassloader);
            }
            tmpClassloader = parentClassloader;
            parentClassloader = parentClassloader.getParent();
        }
    }

    private void testSomeClassExist(ClassLoader classLoader) {
        try {
            classLoader.loadClass("com.ss.android.common.app.ActivityStackManager");
            XcustomBridge.log("ActivityStackManager exist");
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("hook1", Log.getStackTraceString(e));
        }
    }


    /**
     * 01-06 00:31:12.925 12000-12060/com.sup.android.superb W/System.err: java.lang.IllegalArgumentException: Expected receiver of type dalvik.system.BaseDexClassLoader, but got com.bytedance.frameworks.plugin.core.DelegateClassLoader
     * 01-06 00:31:12.925 12000-12060/com.sup.android.superb W/System.err:     at java.lang.reflect.Field.get(Native Method)
     * 01-06 00:31:12.925 12000-12060/com.sup.android.superb W/System.err:     at com.l.sevenclasshook.hook.Hook3.getFieldOjbect(Hook3.java:206)
     * 01-06 00:31:12.925 12000-12060/com.sup.android.superb W/System.err:     at com.l.sevenclasshook.hook.Hook3.TestClassloader(Hook3.java:259)
     * 01-06 00:31:12.925 12000-12060/com.sup.android.superb W/System.err:     at com.l.sevenclasshook.hook.Hook3.fart(Hook3.java:240)
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err:     at com.l.sevenclasshook.hook.Hook3$3$1.run(Hook3.java:95)
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err:     at java.lang.Thread.run(Thread.java:818)
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err: java.lang.NullPointerException: null receiver
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err:     at java.lang.reflect.Field.get(Native Method)
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err:     at com.l.sevenclasshook.hook.Hook3.getFieldOjbect(Hook3.java:206)
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err:     at com.l.sevenclasshook.hook.Hook3.TestClassloader(Hook3.java:260)
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err:     at com.l.sevenclasshook.hook.Hook3.fart(Hook3.java:240)
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err:     at com.l.sevenclasshook.hook.Hook3$3$1.run(Hook3.java:95)
     * 01-06 00:31:12.926 12000-12060/com.sup.android.superb W/System.err:     at java.lang.Thread.run(Thread.java:818)
     *
     * @param appClassloader
     */

    public void TestClassloader(ClassLoader appClassloader) {
        if (appClassloader instanceof BaseDexClassLoader) {

        } else if (appClassloader instanceof ClassLoader) {
            List<String> nameList = new ArrayList<>();

            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/8958236_classlist.txt")),
                        "UTF-8"));
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    if (!TextUtils.isEmpty(lineTxt)) {
                        String name = lineTxt.replace("L", "").replaceAll("/", ".").replace(";", "");
                        Log.e("hook1", "after replace name:" + name);
                        nameList.add(name);
                    }
                }
                br.close();
            } catch (Exception e) {
                Log.e("hook1", Log.getStackTraceString(e));
            }


            for (String name : nameList) {
                try {
                    Log.e("Hook1", "===================>loadClass:" + name);
                    appClassloader.loadClass(name);
                } catch (Exception e) {
                    Log.e("Hook1", Log.getStackTraceString(e));
                }
            }

            return;
        }


        Field pathList_Field = (Field) getClassField(appClassloader, "dalvik.system.BaseDexClassLoader", "pathList");
        Object pathList_object = getFieldOjbect("dalvik.system.BaseDexClassLoader", appClassloader, "pathList");
        Object[] ElementsArray = (Object[]) getFieldOjbect("dalvik.system.DexPathList", pathList_object, "dexElements");
        Field dexFile_fileField = null;
        try {
            dexFile_fileField = (Field) getClassField(appClassloader, "dalvik.system.DexPathList$Element", "dexFile");
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        Class DexFileClazz = null;
        try {
            DexFileClazz = appClassloader.loadClass("dalvik.system.DexFile");
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        Method getClassNameList_method = null;
        Method XcustomMethodCode_method = null;
        for (Method field : DexFileClazz.getDeclaredMethods()) {
            if (field.getName().equals("getClassNameList")) {
                getClassNameList_method = field;
                getClassNameList_method.setAccessible(true);
            }
            if (field.getName().equals("XcustomMethodCode")) {
                XcustomMethodCode_method = field;
                XcustomMethodCode_method.setAccessible(true);
            }
        }
        Field mCookiefield = getClassField(appClassloader, "dalvik.system.DexFile", "mCookie");
        if (ElementsArray == null) {
            Log.e("hook1", "element null");
            Log.e("hook1", "element null");
            Log.e("hook1", "element null");
            Log.e("hook1", "element null");
            Log.e("hook1", "element null");
            Log.e("hook1", "element null");
            Log.e("hook1", "element null");
            return;
        }
        Log.e("ActivityThread->methods", "dalvik.system.DexPathList.ElementsArray.length:" + ElementsArray.length);//5个
        for (int j = 0; j < ElementsArray.length; j++) {
            Object element = ElementsArray[j];
            Object dexfile = null;
            try {
                dexfile = (Object) dexFile_fileField.get(element);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            if (dexfile == null) {
                Log.e("ActivityThread", "dexfile is null");
                continue;
            }
            if (dexfile != null) {


                Object mcookie = getClassFieldObject(appClassloader, "dalvik.system.DexFile", dexfile, "mCookie");
                if (mcookie == null) {
                    Object mInternalCookie = getClassFieldObject(appClassloader, "dalvik.system.DexFile", dexfile, "mInternalCookie");
                    if (mInternalCookie != null) {
                        mcookie = mInternalCookie;
                    } else {
                        Log.v("ActivityThread->err", "get mInternalCookie is null");
                        continue;
                    }

                }
                String[] classnames = null;
                try {
                    classnames = (String[]) getClassNameList_method.invoke(dexfile, mcookie);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                } catch (Error e) {
                    e.printStackTrace();
                    continue;
                }
                if (classnames != null) {
                    for (String eachclassname : classnames) {
                        Log.e("Hook1", "BaseDexClassLoader->TestClassLoader:classname->" + eachclassname);
//                        loadClassAndInvoke(appClassloader, eachclassname, XcustomMethodCode_method);
                        try {
                            appClassloader.loadClass(eachclassname);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        return;
    }

    public void loadClassAndInvoke(ClassLoader appClassloader, String eachclassname, Method dumpMethodCode_method) {
        Class resultclass = null;
        Log.i("ActivityThread", "go into loadClassAndInvoke->" + "classname:" + eachclassname);
        try {
            resultclass = appClassloader.loadClass(eachclassname);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } catch (Error e) {
            e.printStackTrace();
            return;
        }

        if (resultclass != null) {
            try {
                Constructor<?> cons[] = resultclass.getDeclaredConstructors();
                for (Constructor<?> constructor : cons) {
                    if (dumpMethodCode_method != null) {
                        try {
                            dumpMethodCode_method.invoke(null, constructor);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        } catch (Error e) {
                            e.printStackTrace();
                            continue;
                        }
                    } else {
                        Log.e("ActivityThread", "dumpMethodCode_method is null ");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            try {
                Method[] methods = resultclass.getDeclaredMethods();
                if (methods != null) {
                    for (Method m : methods) {
                        if (dumpMethodCode_method != null) {
                            try {
                                dumpMethodCode_method.invoke(null, m);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            } catch (Error e) {
                                e.printStackTrace();
                                continue;
                            }
                        } else {
                            Log.e("ActivityThread", "dumpMethodCode_method is null ");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Process: com.sup.android.superb, PID: 2395
     * java.lang.NoSuchFieldError: com.bytedance.frameworks.plugin.core.DelegateClassLoader#pathList
     * at de.robv.android.xcustom.XcustomHelpers.findField(XcustomHelpers.java:105)
     * at de.robv.android.xcustom.XcustomHelpers.getObjectField(XcustomHelpers.java:894)
     * at com.l.sevenclasshook.hook.Hook3.GetClassLoaderClasslist(Hook3.java:437)
     * at com.l.sevenclasshook.hook.Hook3.fart(Hook3.java:263)
     * at com.l.sevenclasshook.hook.Hook3$3$2.run(Hook3.java:121)
     * at java.lang.Thread.run(Thread.java:818)
     *
     * @param classLoader
     */

    public void GetClassLoaderClasslist(ClassLoader classLoader) {
        //private final DexPathList pathList;
        //public static java.lang.Object getObjectField(java.lang.Object obj, java.lang.String fieldName)
        XcustomBridge.log("start dealwith classloader:" + classLoader);
        Object pathListObj = XcustomHelpers.getObjectField(classLoader, "pathList");
        //private final Element[] dexElements;
        Object[] dexElementsObj = (Object[]) XcustomHelpers.getObjectField(pathListObj, "dexElements");
        for (Object i : dexElementsObj) {
            //private final DexFile dexFile;
            Object dexFileObj = XcustomHelpers.getObjectField(i, "dexFile");
            //private Object mCookie;
            Object mCookieObj = XcustomHelpers.getObjectField(dexFileObj, "mCookie");
            //private static native String[] getClassNameList(Object cookie);
            //    public static java.lang.Object callStaticMethod(java.lang.Class<?> clazz, java.lang.String methodName, java.lang.Object... args) { /* compiled code */ }
            Class DexFileClass = XcustomHelpers.findClass("dalvik.system.DexFile", classLoader);

            String[] classlist = (String[]) XcustomHelpers.callStaticMethod(DexFileClass, "getClassNameList", mCookieObj);
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
        XcustomBridge.log("end dealwith classloader:" + classLoader);

    }
}
