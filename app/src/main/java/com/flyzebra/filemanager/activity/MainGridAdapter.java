package com.flyzebra.filemanager.activity;

import java.util.HashMap;
import java.util.List;

import com.flyzebra.customView.AutoImageView;
import com.flyzebra.filemanager.R;
import com.flyzebra.filemanager.tools.BitmapTools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainGridAdapter extends BaseAdapter {
	private int thread_num = 0;
	private List<HashMap<String, Object>> list = null;
	private String key[] = null;
	private int id[] = null;
	private int idListview;
	private LayoutInflater inflater = null;
	private MyApp myapp;
//	private upImageThread upImageThread = new upImageThread();
	private Context context;

	/* 文件类型,数字为奇数为选中状态，程序中只以+1 -1 表示 */
	private final int TYPE_ROOT = 0;
	private final int TYPE_SDCARD = 2;
	private final int TYPE_DIR1 = 4;
	private final int TYPE_DIR1_NET = 6;
	private final int TYPE_DIR2 = 52;
	private final int TYPE_DIR2_NET = 54;
	private final int TYPE_FILE = 56;

	private class ViewHolder {
		public AutoImageView iv01 = null;
		public TextView tv01 = null;
		public LinearLayout lr01 = null;
	}

	public MainGridAdapter(Context context, List<HashMap<String, Object>> list,
			int idListview, String[] key, int[] id, MyApp myapp) {
		this.myapp = myapp;
		inflater = LayoutInflater.from(context);
		this.idListview = idListview;
		this.list = list;
		this.key = new String[key.length];
		this.id = new int[id.length];
		System.arraycopy(key, 0, this.key, 0, key.length);
		System.arraycopy(id, 0, this.id, 0, id.length);
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(idListview, null);
			holder.iv01 = (AutoImageView) convertView.findViewById(id[0]);
			holder.tv01 = (TextView) convertView.findViewById(id[1]);
			holder.lr01 = (LinearLayout) convertView.findViewById(id[2]);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 动态设置ImageView高度
		LayoutParams para = holder.iv01.getLayoutParams();
		para.width = MainActivity.width;
		para.height = MainActivity.width;
		holder.iv01.setLayoutParams(para);
		// 获取类型
		int TYPE = (Integer) list.get(position).get(key[0]);

		// 设置图标背景
		if (TYPE % 2 == 1) {
			holder.lr01.setBackgroundColor(Color.BLUE);
			TYPE--;
		} else {
			holder.lr01.setBackgroundColor(Color.WHITE);
		}
		// 设置图标
		switch (TYPE) {
		case TYPE_ROOT:
			holder.iv01.setImageResource(R.drawable.icon_root);
			break;
		case TYPE_SDCARD:
			holder.iv01.setImageResource(R.drawable.icon_card);
			break;
		case TYPE_DIR1:
			holder.iv01.setImageResource(R.drawable.icon_mobile);
			break;
		case TYPE_DIR1_NET:
			holder.iv01.setImageResource(R.drawable.icon_network);
			break;
		case TYPE_DIR2:
		case TYPE_DIR2_NET:
			holder.iv01.setImageResource(R.drawable.icon_dirg1);
			break;
		case TYPE_FILE:
			String filename = (String) myapp.getList().get(position)
					.get("NAME");
			int last = filename.lastIndexOf(".");
			if (last < 0) {
				holder.iv01.setImageResource(R.drawable.icon_file1);
			} else {
				String filetype = filename.substring(last, filename.length());
				if (filetype.equalsIgnoreCase(".mp3")) {
					Log.i("FILETYEP", filetype);
					holder.iv01.setImageResource(R.drawable.icon_mp31);
				} else if (filetype.equalsIgnoreCase(".wma")
						|| filetype.equalsIgnoreCase(".wav")) {
					holder.iv01.setImageResource(R.drawable.icon_audio1);
				} else if (filetype.equalsIgnoreCase(".rmvb")
						|| filetype.equalsIgnoreCase(".rm")
						|| filetype.equalsIgnoreCase(".avi")
						|| filetype.equalsIgnoreCase(".wmv")
						|| filetype.equalsIgnoreCase(".mp4")) {
					holder.iv01.setImageResource(R.drawable.icon_video1);
					break;
				} else if (filetype.equalsIgnoreCase(".pdf")) {
					holder.iv01.setImageResource(R.drawable.icon_pdf1);
					break;
				} else if (filetype.equalsIgnoreCase(".txt")
						|| filetype.equalsIgnoreCase(".doc")
						|| filetype.equalsIgnoreCase(".wps")
						|| filetype.equalsIgnoreCase(".xml")) {
					holder.iv01.setImageResource(R.drawable.icon_txt1);
					break;
				} else if (filetype.equalsIgnoreCase(".jpg")
						|| filetype.equalsIgnoreCase(".bmp")
						|| filetype.equalsIgnoreCase(".png")
						|| filetype.equalsIgnoreCase(".gif")) {
					Bitmap bm = (Bitmap) list.get(position).get("minbm");
					if (bm == null) {
						bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_image);
						list.get(position).put("POSITION", position);
						list.get(position).put("minbm", bm);
//						myapp.getUpIamge_list().add(list.get(position));
//						thread_num++;
//						new Thread(upImageThread).start();
					}
					holder.iv01.setImageBitmap(bm);
				} else {
					holder.iv01.setImageResource(R.drawable.icon_file1);
					break;
				}
				break;

			}
		}
		holder.tv01.setText((String) list.get(position).get(key[1]));
		return convertView;
	}

	// public static Drawable getApkIcon(Context context, String apkPath) {
	// PackageManager pm = context.getPackageManager();
	// PackageInfo info = pm.getPackageArchiveInfo(apkPath,
	// PackageManager.GET_ACTIVITIES);
	// if (info != null) {
	// ApplicationInfo appInfo = info.applicationInfo;
	// appInfo.sourceDir = apkPath;
	// appInfo.publicSourceDir = apkPath;
	// try {
	// return appInfo.loadIcon(pm);
	// } catch (OutOfMemoryError e) {
	// Log.e("ApkIconLoader", e.toString());
	// }
	// }
	// return null;
	// }
	public class upImageThread implements Runnable {
		@Override
		public void run() {			
			if (thread_num > 1) {
				thread_num--;
			} else {
				while (!myapp.getUpIamge_list().isEmpty()) {
					String spath = (String) myapp.getUpIamge_list().get(0).get("PATH");
//					Log.i(MyApp.TAG,""+spath);
					int position = (Integer) myapp.getUpIamge_list().get(0).get("POSITION");
					Bitmap bm = BitmapTools.GetMinBitmapFormFile(spath, MainActivity.width*90/100);
					if (!myapp.getUpIamge_list().isEmpty()) {
//						if (spath.equals((String) myapp.getUpIamge_list().get(0).get("PATH"))) {
							myapp.getUpIamge_list().remove(0);
//						}
					}
//					Log.i(MyApp.TAG,""+spath);
					while (MyApp.upImage_map_lock) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					MyApp.upImage_map_lock = true;
					myapp.getUpImage_map().clear();
//					myapp.getUpImage_map().put("PATH", spath);
					myapp.getUpImage_map().put("minbm", bm);
					myapp.getUpImage_map().put("POSITION", position);
					myapp.SendMainBroadcast("BROADCAST_MAIN_UPIMAGE");					
				}
				thread_num--;
				Log.i(MyApp.TAG,""+myapp.getUpIamge_list());
			}
			
		}
	}

}
