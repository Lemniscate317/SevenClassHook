//package com.l.sevenclasshook.hook;
//
//import android.os.Bundle;
//import android.util.Log;
//
//import com.tencent.qq.game.IXposedHookLoadPackage;
//import com.tencent.qq.game.XC_MethodHook;
//import com.tencent.qq.game.XposedHelpers;
//import com.tencent.qq.game.callbacks.XC_LoadPackage;
//
//import de.robv.android.xcustom.IXcustomHookLoadPackage;
//import de.robv.android.xcustom.callbacks.XC_LoadPackage;
//
//
//public class Hook2 implements IXcustomHookLoadPackage {
//    String[] strs = new String[122 - 48];
//
//
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//
//
//
//        if (lpparam.packageName.equals("com.kanxue.test2")) {
//            ClassLoader classLoader = lpparam.classLoader;
//            Class<?> mainActivity = classLoader.loadClass("com.kanxue.test2.MainActivity");
//
//            for (int i = 48; i < 122; i++) {
//                char c = (char) i;
//                strs[i - 48] = String.valueOf(c);
//            }
//
//            XposedHelpers.findAndHookMethod(mainActivity, "onCreate", Bundle.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//
//                    System.loadLibrary("sandhook-native");
//
//
////                    for (int i = 0; i < strs.length; i++) {
////                        for (int j = 0; j < strs.length; j++) {
////                            for (int k = 0; k < strs.length; k++) {
////                                String key = strs[i] + strs[j] + strs[k];
////
////
////                                boolean result = (boolean) XposedHelpers.callMethod(param.thisObject, "jnitest", key);
////                                if (result) {
////                                    Log.e("Hook", "key:" + key + "   result:" + result);
////                                }
////                            }
////                        }
////                    }
//                }
//            });
//
//
//        }
//    }
//}
