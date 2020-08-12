//package com.l.sevenclasshook.hook;
//
//import android.app.Application;
//import android.content.Context;
//import android.util.Log;
//
//import com.tencent.qq.game.IXposedHookLoadPackage;
//import com.tencent.qq.game.XC_MethodHook;
//import com.tencent.qq.game.XposedBridge;
//import com.tencent.qq.game.XposedHelpers;
//import com.tencent.qq.game.callbacks.XC_LoadPackage;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
//
//
//public class Hook1 implements IXposedHookLoadPackage {
//
//
//    private Context mContext;
//
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//
//        if (lpparam.packageName.equals("com.aipao.hanmoveschool")) {
//
//
//            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//
//                    mContext = (Context) param.args[0];
//                    //System.loadLibrary("sandhook-native");
//
//                    XposedHelpers.callMethod(Runtime.getRuntime(), "doLoad", "/system/lib64/libsandhook-native.so", mContext.getClassLoader());
//
//                    GetClassLoaderClasslist(mContext.getClassLoader());
//                }
//            });
//
//
//        }
//    }
//
//    public static Field getClassField(ClassLoader classloader, String class_name,
//                                      String filedName) {
//
//        try {
//            Class obj_class = classloader.loadClass(class_name);//Class.forName(class_name);
//            Field field = obj_class.getDeclaredField(filedName);
//            field.setAccessible(true);
//            return field;
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }
//
//    public static Object getClassFieldObject(ClassLoader classloader, String class_name, Object obj,
//                                             String filedName) {
//
//        try {
//            Class obj_class = classloader.loadClass(class_name);//Class.forName(class_name);
//            Field field = obj_class.getDeclaredField(filedName);
//            field.setAccessible(true);
//            Object result = null;
//            result = field.get(obj);
//            return result;
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }
//
//    public static Object invokeStaticMethod(String class_name,
//                                            String method_name, Class[] pareTyple, Object[] pareVaules) {
//
//        try {
//            Class obj_class = Class.forName(class_name);
//            Method method = obj_class.getMethod(method_name, pareTyple);
//            return method.invoke(null, pareVaules);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }
//
//    public static Object getFieldOjbect(String class_name, Object obj,
//                                        String filedName) {
//        try {
//            Class obj_class = Class.forName(class_name);
//            Field field = obj_class.getDeclaredField(filedName);
//            field.setAccessible(true);
//            return field.get(obj);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }
//
//    public static ClassLoader getClassloader() {
//        ClassLoader resultClassloader = null;
//        Object currentActivityThread = invokeStaticMethod(
//                "android.app.ActivityThread", "currentActivityThread",
//                new Class[]{}, new Object[]{});
//        Object mBoundApplication = getFieldOjbect(
//                "android.app.ActivityThread", currentActivityThread,
//                "mBoundApplication");
//        Application mInitialApplication = (Application) getFieldOjbect("android.app.ActivityThread",
//                currentActivityThread, "mInitialApplication");
//        Object loadedApkInfo = getFieldOjbect(
//                "android.app.ActivityThread$AppBindData",
//                mBoundApplication, "info");
//        Application mApplication = (Application) getFieldOjbect("android.app.LoadedApk", loadedApkInfo, "mApplication");
//        resultClassloader = mApplication.getClassLoader();
//        return resultClassloader;
//    }
//
//    public void GetClassLoaderClasslist(ClassLoader classLoader) {
//        //private final DexPathList pathList;
//        //public static java.lang.Object getObjectField(java.lang.Object obj, java.lang.String fieldName)
//        XposedBridge.log("start dealwith classloader:" + classLoader);
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
//            String[] classlist = (String[]) XposedHelpers.callStaticMethod(DexFileClass, "getClassNameList", mCookieObj);
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
//        XposedBridge.log("end dealwith classloader:" + classLoader);
//
//    }
//}
