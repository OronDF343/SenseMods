/*
 * Copyright (C) 2016 OronDF343
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.orondf343.android.sensemods;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class SenseMods implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if("com.htc.launcher".equals(lpparam.packageName)) {
            // Infinifolders
            XposedBridge.log("[SenseMods] Hooked to Sense Home process");
            final Class<?> folderInfoClass = findClass("com.htc.launcher.folder.FolderInfo", lpparam.classLoader);
            final Class<?> itemInfoClass = findClass("com.htc.launcher.ItemInfo", lpparam.classLoader);
            findAndHookMethod("com.htc.launcher.folder.Folder", lpparam.classLoader, "getFolderMaxPages", folderInfoClass, Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object folderInfo = param.args[0];
                    Integer folderType = (Integer) itemInfoClass.getDeclaredMethod("getItemType").invoke(folderInfo);
                    if (folderType == 3) param.setResult(-1);
                }
            });
        } else if ("android".equals(lpparam.packageName)) {
            // Volume to wake:
            findAndHookMethod("android.view.KeyEvent", lpparam.classLoader, "isWakeKey", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.args[0].equals(24) || param.args[0].equals(25)) param.setResult(true);
                }
            });
            findAndHookMethod("com.android.server.policy.PhoneWindowManager", lpparam.classLoader, "isWakeKeyWhenScreenOff", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.args[0].equals(24) || param.args[0].equals(25)) param.setResult(true);
                }
            });
            XposedBridge.log("[SenseMods] Hooked isWakeKey and isWakeKeyWhenScreenOff");
        }
    }
}
