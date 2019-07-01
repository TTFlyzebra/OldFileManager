package com.flyzebra.filemanager.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

/**
 * @author Administrator
 *
 */
public class APKTools {

	public static final String TAG = "com.flyzebra";

	/**
	 * 安装指定APK文件
	 */
	public static void InstallAPK(Context context, File file) {
		Intent mIntent = new Intent(Intent.ACTION_VIEW);
		mIntent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		context.startActivity(mIntent);
	}

	/**
	 * 返回本机安装的应用程序列表
	 */
	public static List<Map<String, Object>> getLocalApp(Context context,int width,int height) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		PackageManager pckMan = context.getPackageManager();
		List<PackageInfo> packs = pckMan.getInstalledPackages(0);
		int count = packs.size();
		for (int i = 0; i < count; i++) {
			PackageInfo pi = packs.get(i);
			// 如果是系统应用不添加
			if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				String packname = pi.applicationInfo.packageName;
				String appname = pi.applicationInfo.loadLabel(context.getPackageManager()).toString();
				Drawable appicon = pi.applicationInfo.loadIcon(pckMan);
				BitmapDrawable bd = (BitmapDrawable) appicon;
				Bitmap appbitmap = bd.getBitmap();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("appname", appname);
				map.put("packname", packname);
				map.put("appbitmap", appbitmap);
				list.add(map);
			}
		}
		return list;
	}
	
	
	/**
	 * 按指定包名和类别启动应用程序
	 */
	public static void runApp(Context context, String pkg, String cls) {
		Intent intent = new Intent();
		ComponentName comp = new ComponentName(pkg, cls);
		intent.setComponent(comp);
		intent.setAction("android.intent.action.MAIN");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 按指定包名运行应用程序
	 */
	public static boolean runAppByPackname(Context context, String packagename) {
		boolean flag = false;
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(packagename, 0);
			if (pi == null) {
				return flag;
			}
			Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
			mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			mIntent.setPackage(pi.packageName);
			List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(mIntent, 0);
			ResolveInfo resolveinfo = resolveinfoList.iterator().next();
			if (resolveinfo != null) {
				String packageName = resolveinfo.activityInfo.packageName;
				String className = resolveinfo.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				ComponentName cn = new ComponentName(packageName, className);
				intent.setComponent(cn);
				context.startActivity(intent);
			}
			flag = true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}// 此处启动某些未知程序会抛出异常
		catch (NoSuchElementException e) {
			e.printStackTrace();
			Log.i(TAG,"执行APP失败-->"+e.toString());
		}
		return flag;
	}

	/**
	 * 按指定包名获取应用程序的图标
	 */
	public static Bitmap getLocalAppBitmap(Context context, String pkname, int width, int height) {
		PackageManager pckMan = context.getPackageManager();
		List<PackageInfo> packs = pckMan.getInstalledPackages(0);
		int count = packs.size();
		for (int i = 0; i < count; i++) {
			PackageInfo pi = packs.get(i);
			// 如果是系统应用不添加& ApplicationInfo.FLAG_SYSTEM
			if ((pi.applicationInfo.flags ) == 0) {
				String packname = pi.applicationInfo.packageName;
				if (packname.equals(pkname)) {
					Drawable appicon = pi.applicationInfo.loadIcon(context.getPackageManager());
					BitmapDrawable db = (BitmapDrawable) appicon;
					return db.getBitmap();
				}
			}
		}
		return null;
	}

}
