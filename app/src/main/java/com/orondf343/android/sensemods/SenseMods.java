package com.orondf343.android.sensemods;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Java project 5776 by Oron Feinerman & Mor Sofer.
 */
public class SenseMods implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(!"com.htc.launcher".equals(lpparam.packageName)) {
            return;
        }
        XposedBridge.log("[SenseMods] Attached to Sense Home process");
        final Class<?> folderInfoClass = findClass("com.htc.launcher.folder.FolderInfo", lpparam.classLoader);
        final Class<?> itemInfoClass = findClass("com.htc.launcher.ItemInfo", lpparam.classLoader);
        findAndHookMethod("com.htc.launcher.folder.Folder", lpparam.classLoader, "getFolderMaxPages", folderInfoClass, Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object folderInfo = param.args[0];
                Integer folderType = (Integer) itemInfoClass.getDeclaredMethod("getItemType").invoke(folderInfo);
                if (folderType == 3) param.setResult(-1);
            }
        });
    }
}
