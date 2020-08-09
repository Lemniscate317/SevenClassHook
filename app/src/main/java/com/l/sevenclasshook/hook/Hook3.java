package com.l.sevenclasshook.hook;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.qq.game.IXposedHookLoadPackage;
import com.tencent.qq.game.XC_MethodHook;
import com.tencent.qq.game.XposedBridge;
import com.tencent.qq.game.XposedHelpers;
import com.tencent.qq.game.callbacks.XC_LoadPackage;


public class Hook3 implements IXposedHookLoadPackage {
    private Context mContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals("com.sup.android.superb")) {
            XposedBridge.log(lpparam.packageName + "  enter");
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    XposedBridge.log("attach after");

                    mContext = (Context) param.args[0];

                    //Toast.makeText(mContext,"after attach",Toast.LENGTH_SHORT).show();

                    //System.loadLibrary("sandhook-native");

                    XposedHelpers.callMethod(Runtime.getRuntime(), "doLoad", "/system/lib/libnative-lib.so", mContext.getClassLoader());

                    //GetClassLoaderClasslist(mContext.getClassLoader());
                }
            });
        }
    }

    public void GetClassLoaderClasslist(ClassLoader classLoader) {
        //private final DexPathList pathList;
        //public static java.lang.Object getObjectField(java.lang.Object obj, java.lang.String fieldName)
        XposedBridge.log("start dealwith classloader:" + classLoader);
        Object pathListObj = XposedHelpers.getObjectField(classLoader, "pathList");
        //private final Element[] dexElements;
        Object[] dexElementsObj = (Object[]) XposedHelpers.getObjectField(pathListObj, "dexElements");
        for (Object i : dexElementsObj) {
            //private final DexFile dexFile;
            Object dexFileObj = XposedHelpers.getObjectField(i, "dexFile");
            //private Object mCookie;
            Object mCookieObj = XposedHelpers.getObjectField(dexFileObj, "mCookie");
            //private static native String[] getClassNameList(Object cookie);
            //    public static java.lang.Object callStaticMethod(java.lang.Class<?> clazz, java.lang.String methodName, java.lang.Object... args) { /* compiled code */ }
            Class DexFileClass = XposedHelpers.findClass("dalvik.system.DexFile", classLoader);

            String[] classlist = (String[]) XposedHelpers.callStaticMethod(DexFileClass, "getClassNameList", mCookieObj);
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
        XposedBridge.log("end dealwith classloader:" + classLoader);

    }
}
