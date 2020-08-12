package com.l.sevenclasshook.hook;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.robv.android.xcustom.IXcustomHookLoadPackage;
import de.robv.android.xcustom.XC_MethodHook;
import de.robv.android.xcustom.XcustomBridge;
import de.robv.android.xcustom.XcustomHelpers;
import de.robv.android.xcustom.callbacks.XC_LoadPackage;


public class Hook3 implements IXcustomHookLoadPackage {
    private Context mContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XcustomBridge.log("test can log");
        if (lpparam.packageName.equals("com.sup.android.superb")) {
            XcustomBridge.log(lpparam.packageName + "  enter");
            XcustomHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    XcustomBridge.log("attach after");

                    mContext = (Context) param.args[0];

                    //Toast.makeText(mContext,"after attach",Toast.LENGTH_SHORT).show();

                    //System.loadLibrary("sandhook-native");

                    XcustomHelpers.callMethod(Runtime.getRuntime(), "doLoad", "/system/lib/libnative-lib.so", mContext.getClassLoader());

                    GetClassLoaderClasslist(mContext.getClassLoader());
                }
            });
        }
    }

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
