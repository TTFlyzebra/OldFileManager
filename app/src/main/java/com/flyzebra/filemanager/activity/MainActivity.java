package com.flyzebra.filemanager.activity;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import com.flyzebra.filemanager.R;
import com.flyzebra.filemanager.activity.MainListAdapter.OnCheckBoxClick;
import com.flyzebra.filemanager.sqlite.DBhelper;
import com.flyzebra.filemanager.tools.FtpTools;
import com.flyzebra.filemanager.tools.MobilePC;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class MainActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener, OnClickListener, OnCheckBoxClick {
	// private final String TAG = "FileManager";
	private MyApp myapp;
	private TextView main_tv01 = null;
	private final int MAIN_HANDLER_DLGLOGIN = 1;
	private final int MAIN_HANDLER_DELOK = 2;
	private final int MAIN_HANDLER_NETOK = 3;
	private final int MAIN_HANDLER_NONET = 4;
	private ListNetworkFileTask tasknetwork = null;
	private ListLocalFileTask tasklocal = null;
	private ProgressDialog progressdialog = null;
	private boolean exitonBackPressed = false;
	private ArrayList<HashMap<String, Object>> selectList = null;

	private TextView dlg_copyfile_tv01 = null;
	private TextView dlg_copyfile_tv02 = null;
	private SeekBar dlg_copyfile_sb01 = null;
	private AlertDialog dlg_copyfile = null;
	private boolean dlg_copyfile_isshow = false;
	public static int width = 0;

	/* 广播 */
	private final static String MAIN_ACTION_BROADCAST_UPVIEW = "BROADCAST_MAIN_UPVIEW";
	private final static String MAIN_ACTION_BROADCAST_UPDLGCOPY_MAX = "BROADCAST_MAIN_UPDLGCOPY_MAX";
	private final static String MAIN_ACTION_BROADCAST_UPDLGCOPY_PRO = "BROADCAST_MAIN_UPDLGCOPY_PRO";
	private final static String MAIN_ACTION_BROADCAST_UPDLGCOPY_END = "BROADCAST_MAIN_UPDLGCOPY_END";
	private final static String MAIN_ACTION_BROADCAST_UPIMAGE = "BROADCAST_MAIN_UPIMAGE";
	private MainBroadcast broadcast = null;

	private float scale = 0;
	private float screenWidth = 0;
	private float screenHeight = 0;

	private final int SHOW_HOME = 0;
	private final int SHOW_LOCAL = 1;
	private final int SHOW_NETWORK = 2;

	/* 文件类型,数字为奇数为选中状态，程序中只以+1 -1 表示 */
	private final int TYPE_ROOT = 0;
	private final int TYPE_SDCARD = 2;
	private final int TYPE_DIR1 = 4;
	private final int TYPE_DIR1_NET = 6;
	private final int TYPE_DIR2 = 52;
	private final int TYPE_DIR2_NET = 54;
	private final int TYPE_FILE = 56;

	/* 列表网格显示样式 */
	private final int VIEW_LIST1 = 1;
	private final int VIEW_GRID4 = 2;
	private final int VIEW_GRID5 = 3;
	private final int VIEW_GRID6 = 4;

	/* 定义当前自定义菜单的显示和隐藏 */
	private final int MENU_NORMAL = 0;
	private final int MENU_SELECT = 1;
	private final int MENU_COPY = 2;

	/* 定义复制的文件是在网络上还是在本机，是复制还是剪切 */
	private final int MODE_NONECOPY = 0;
	private final int MODE_NETCOPY = 1;
	private final int MODE_NETCUT = 2;
	private final int MODE_LOCCOPY = 3;
	private final int MODE_LOCCUT = 4;
	private final int MODE_ISCOPY = 5;

	private MainListAdapter listadapter = null;
	private MainGridAdapter gridadapter = null;
	private ListView main_lv01 = null;
	private GridView main_gv01 = null;
	/* Menu */
	private LinearLayout horizontal_menu = null;
	private LinearLayout vertical_menu = null;

	private LinearLayout menu01_v_view = null;
	private LinearLayout menu01_h_view = null;
	private LinearLayout menu01_v_rename = null;
	private LinearLayout menu01_h_rename = null;
	private LinearLayout menu01_v_del = null;
	private LinearLayout menu01_h_del = null;
	private LinearLayout menu01_v_copy = null;
	private LinearLayout menu01_h_copy = null;
	private LinearLayout menu01_v_cut = null;
	private LinearLayout menu01_h_cut = null;
	private LinearLayout menu01_v_back = null;
	private LinearLayout menu01_h_back = null;
	private LinearLayout menu01_v_newfile = null;
	private LinearLayout menu01_h_newfile = null;
	private LinearLayout menu01_v_cancel = null;
	private LinearLayout menu01_h_cancel = null;
	private LinearLayout menu01_v_paset = null;
	private LinearLayout menu01_h_paset = null;
	private LinearLayout menu01_v_home = null;
	private LinearLayout menu01_h_home = null;
	private LinearLayout menu01_v_find = null;
	private LinearLayout menu01_h_find = null;
	private LinearLayout menu01_v_share = null;
	private LinearLayout menu01_h_share = null;
	private LinearLayout menu01_v_exit = null;
	private LinearLayout menu01_h_exit = null;
	private LinearLayout menu01_v_adddir = null;
	private LinearLayout menu01_h_adddir = null;
	private LinearLayout menu01_v_addnetwork = null;
	private LinearLayout menu01_h_addnetwork = null;
	private LinearLayout menu01_v_all = null;
	private LinearLayout menu01_h_all = null;

	private FtpServer mFtpServer = null;
	private File mFtpConfigfile = null;
	private int ftpPort = 2121;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		myapp = (MyApp) getApplication();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/* 获取窗口宽度高度 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		if (screenWidth > screenHeight) {
			scale = screenWidth / screenHeight;

		} else {
			scale = screenHeight / screenWidth;
		}

		/* 注册广播 */
		broadcast = new MainBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MAIN_ACTION_BROADCAST_UPVIEW);
		filter.addAction(MAIN_ACTION_BROADCAST_UPDLGCOPY_MAX);
		filter.addAction(MAIN_ACTION_BROADCAST_UPDLGCOPY_PRO);
		filter.addAction(MAIN_ACTION_BROADCAST_UPDLGCOPY_END);
		filter.addAction(MAIN_ACTION_BROADCAST_UPIMAGE);
		registerReceiver(broadcast, filter);

		main_tv01 = (TextView) findViewById(R.id.main_tv01);
		main_lv01 = (ListView) findViewById(R.id.main_lv01);
		main_gv01 = (GridView) findViewById(R.id.main_gv01);

		horizontal_menu = (LinearLayout) findViewById(R.id.horizontal_menu);
		vertical_menu = (LinearLayout) findViewById(R.id.vertical_menu);

		menu01_v_exit = (LinearLayout) findViewById(R.id.menu01_v_exit);
		menu01_h_exit = (LinearLayout) findViewById(R.id.menu01_h_exit);
		menu01_v_view = (LinearLayout) findViewById(R.id.menu01_v_view);
		menu01_h_view = (LinearLayout) findViewById(R.id.menu01_h_view);
		menu01_v_back = (LinearLayout) findViewById(R.id.menu01_v_back);
		menu01_h_back = (LinearLayout) findViewById(R.id.menu01_h_back);
		menu01_v_newfile = (LinearLayout) findViewById(R.id.menu01_v_newfile);
		menu01_h_newfile = (LinearLayout) findViewById(R.id.menu01_h_newfile);
		menu01_v_cancel = (LinearLayout) findViewById(R.id.menu01_v_cancel);
		menu01_h_cancel = (LinearLayout) findViewById(R.id.menu01_h_cancel);
		menu01_v_rename = (LinearLayout) findViewById(R.id.menu01_v_rename);
		menu01_h_rename = (LinearLayout) findViewById(R.id.menu01_h_rename);
		menu01_v_del = (LinearLayout) findViewById(R.id.menu01_v_del);
		menu01_h_del = (LinearLayout) findViewById(R.id.menu01_h_del);
		menu01_v_home = (LinearLayout) findViewById(R.id.menu01_v_home);
		menu01_h_home = (LinearLayout) findViewById(R.id.menu01_h_home);
		menu01_v_find = (LinearLayout) findViewById(R.id.menu01_v_find);
		menu01_h_find = (LinearLayout) findViewById(R.id.menu01_h_find);
		menu01_h_share = (LinearLayout) findViewById(R.id.menu01_h_share);
		menu01_v_share = (LinearLayout) findViewById(R.id.menu01_v_share);
		menu01_v_copy = (LinearLayout) findViewById(R.id.menu01_v_copy);
		menu01_h_copy = (LinearLayout) findViewById(R.id.menu01_h_copy);
		menu01_v_cut = (LinearLayout) findViewById(R.id.menu01_v_cut);
		menu01_h_cut = (LinearLayout) findViewById(R.id.menu01_h_cut);
		menu01_v_paset = (LinearLayout) findViewById(R.id.menu01_v_paset);
		menu01_h_paset = (LinearLayout) findViewById(R.id.menu01_h_paset);
		menu01_v_adddir = (LinearLayout) findViewById(R.id.menu01_v_adddir);
		menu01_h_adddir = (LinearLayout) findViewById(R.id.menu01_h_adddir);
		menu01_v_addnetwork = (LinearLayout) findViewById(R.id.menu01_v_addnetwork);
		menu01_h_addnetwork = (LinearLayout) findViewById(R.id.menu01_h_addnetwork);
		menu01_v_all = (LinearLayout) findViewById(R.id.menu01_v_all);
		menu01_h_all = (LinearLayout) findViewById(R.id.menu01_h_all);

		if (listadapter == null) {
			listadapter = new MainListAdapter(this, myapp.getList(),
					R.layout.file_listview, new String[] { "MARK", "NAME",
							"INFO" }, new int[] { R.id.lv01_icon,
							R.id.lv01_tv01, R.id.lv01_tv02, R.id.lv01_item,
							R.id.lv01_chk }, myapp, this);
		}
		if (gridadapter == null) {
			gridadapter = new MainGridAdapter(this, myapp.getList(),
					R.layout.file_gridview, new String[] { "MARK", "NAME",
							"INFO" }, new int[] { R.id.gv01_icon,
							R.id.gv01_tv01, R.id.gv01_item }, myapp);
		}

		main_lv01.setAdapter(listadapter);
		main_gv01.setAdapter(gridadapter);

		main_lv01.setOnItemClickListener(this);
		main_gv01.setOnItemClickListener(this);
		main_gv01.setOnItemLongClickListener(this);

		menu01_v_back.setOnClickListener(this);
		menu01_h_back.setOnClickListener(this);
		menu01_v_view.setOnClickListener(this);
		menu01_h_view.setOnClickListener(this);
		menu01_v_newfile.setOnClickListener(this);
		menu01_h_newfile.setOnClickListener(this);
		menu01_v_cancel.setOnClickListener(this);
		menu01_h_cancel.setOnClickListener(this);
		menu01_v_rename.setOnClickListener(this);
		menu01_h_rename.setOnClickListener(this);
		menu01_v_del.setOnClickListener(this);
		menu01_h_del.setOnClickListener(this);
		menu01_v_copy.setOnClickListener(this);
		menu01_h_copy.setOnClickListener(this);
		menu01_v_cut.setOnClickListener(this);
		menu01_h_cut.setOnClickListener(this);
		menu01_v_paset.setOnClickListener(this);
		menu01_h_paset.setOnClickListener(this);
		menu01_v_home.setOnClickListener(this);
		menu01_h_home.setOnClickListener(this);
		menu01_v_find.setOnClickListener(this);
		menu01_h_find.setOnClickListener(this);
		menu01_v_share.setOnClickListener(this);
		menu01_h_share.setOnClickListener(this);
		menu01_v_exit.setOnClickListener(this);
		menu01_h_exit.setOnClickListener(this);
		menu01_v_adddir.setOnClickListener(this);
		menu01_h_adddir.setOnClickListener(this);
		menu01_v_addnetwork.setOnClickListener(this);
		menu01_h_addnetwork.setOnClickListener(this);
		menu01_v_all.setOnClickListener(this);
		menu01_h_all.setOnClickListener(this);

		progressdialog = new ProgressDialog(this);
		progressdialog.setCanceledOnTouchOutside(false);

		SetViewVisible();
		ShowItem(myapp.getShow());
	}

	private void ShowItem(String show) {
		if (show.equals("HOME")) {
			myapp.getList().clear();
			myapp.getUpIamge_list().clear();
			myapp.setShowwhat(SHOW_HOME);
			SetViewVisible();
			HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("PATH", "/");
			map1.put("NAME", "本机根目录");
			map1.put("MARK", TYPE_ROOT);
			map1.put("INFO", "");
			myapp.getList().add(map1);
			/* 确定SDCARD是不是存在，如存在则打开，如不存在则显示根目录("/") */
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				File f1 = Environment.getExternalStorageDirectory();
				HashMap<String, Object> map2 = new HashMap<String, Object>();
				map2.put("PATH", f1.getAbsolutePath());
				map2.put("NAME", "本机SDCARD-0");
				map2.put("MARK", TYPE_SDCARD);
				map2.put("INFO", "");
				myapp.getList().add(map2);
			}
			DBhelper mydbhelper = new DBhelper(MainActivity.this);
			for (HashMap<String, Object> tempmap : mydbhelper.QueryAll()) {
				switch ((Integer) tempmap.get("MARK")) {
				case TYPE_DIR1:
					tempmap.put("INFO", "");
					myapp.getList().add(tempmap);
					break;
				case TYPE_DIR1_NET:
					tempmap.put("INFO", "");
					myapp.getList().add(tempmap);
					break;
				}
			}
			mydbhelper.close();
			ShowText("首页");
			UpAdapter();
		} else if (show.equals("NEWITEM")) {
			// 如果是新建选项
		} else if (show.lastIndexOf("smb://") == 0) {
			if (tasknetwork == null
					|| tasknetwork.getStatus()
							.equals(AsyncTask.Status.FINISHED)) {
				myapp.setShowwhat(SHOW_NETWORK);
				SetViewVisible();
				tasknetwork = new ListNetworkFileTask();
				tasknetwork.execute(show);
				ShowText(show);
			}
		} else if (show.indexOf("/") == 0) {
			if (tasklocal == null
					|| tasklocal.getStatus().equals(AsyncTask.Status.FINISHED)) {
				myapp.setShowwhat(SHOW_LOCAL);
				SetViewVisible();
				tasklocal = new ListLocalFileTask();
				tasklocal.execute(show);
				ShowText(show);
			}
		}

	}

	@Override
	public void onBackPressed() {
		if (myapp.getPathstack().isEmpty()) {
			if (exitonBackPressed) {
				finish();
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"再按一次退出!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				exitonBackPressed = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						exitonBackPressed = false;
					}
				}).start();
			}
		} else {
			myapp.setShow(myapp.getPathstack().pop());
			ShowItem(myapp.getShow());
		}
	}

	/* 设置各组件的位置 */
	private void SetViewVisible() {
		switch (myapp.getShowwhat()) {
		// 视图
		case SHOW_LOCAL:
		case SHOW_NETWORK:
			switch (myapp.getShowview()) {
			case VIEW_LIST1:
				main_lv01.setVisibility(View.VISIBLE);
				main_gv01.setVisibility(View.INVISIBLE);
				break;
			case VIEW_GRID4:
				main_lv01.setVisibility(View.INVISIBLE);
				main_gv01.setVisibility(View.VISIBLE);
				if (screenHeight < screenWidth) {
					int number = (int) (4 * scale);
					width = (int) (screenWidth / (number));
					main_gv01.setNumColumns(number);
				} else {
					width = (int) (screenWidth / 4);
					main_gv01.setNumColumns(4);
				}
				break;
			case VIEW_GRID5:
				main_lv01.setVisibility(View.INVISIBLE);
				main_gv01.setVisibility(View.VISIBLE);
				if (screenHeight < screenWidth) {
					int number = (int) (5 * scale);
					width = (int) (screenWidth / (number));
					main_gv01.setNumColumns(number);
				} else {
					width = (int) (screenWidth / 5);
					main_gv01.setNumColumns(5);
				}
				break;
			case VIEW_GRID6:
				main_lv01.setVisibility(View.INVISIBLE);
				main_gv01.setVisibility(View.VISIBLE);
				if (screenHeight < screenWidth) {
					int number = (int) (6 * scale);
					width = (int) (screenWidth / (number));
					main_gv01.setNumColumns(number);
				} else {
					width = (int) (screenWidth / 6);
					main_gv01.setNumColumns(6);
				}
				break;
			}
			break;
		case SHOW_HOME:
			main_lv01.setVisibility(View.INVISIBLE);
			main_gv01.setVisibility(View.VISIBLE);
			if (screenHeight < screenWidth) {
				int number = (int) (3 * scale);
				width = (int) (screenWidth / (number));
				main_gv01.setNumColumns(number);
			} else {
				width = (int) (screenWidth / 3);
				main_gv01.setNumColumns(3);
			}
			break;
		}

		// 菜单
		if (screenHeight > screenWidth) {
			horizontal_menu.setVisibility(View.VISIBLE);
			vertical_menu.setVisibility(View.GONE);
			switch (myapp.getShowwhat()) {
			case SHOW_HOME:
				menu01_h_adddir.setVisibility(View.VISIBLE);
				menu01_h_exit.setVisibility(View.VISIBLE);
				menu01_h_addnetwork.setVisibility(View.VISIBLE);
				menu01_h_back.setVisibility(View.GONE);
				menu01_h_home.setVisibility(View.GONE);
				menu01_h_find.setVisibility(View.GONE);
				menu01_h_newfile.setVisibility(View.GONE);
				menu01_h_copy.setVisibility(View.GONE);
				menu01_h_cut.setVisibility(View.GONE);
				menu01_h_paset.setVisibility(View.GONE);
				menu01_h_rename.setVisibility(View.GONE);
				menu01_h_del.setVisibility(View.GONE);
				menu01_h_share.setVisibility(View.GONE);
				menu01_h_all.setVisibility(View.GONE);
				menu01_h_cancel.setVisibility(View.GONE);
				menu01_h_view.setVisibility(View.GONE);
				break;
			case SHOW_LOCAL:
			case SHOW_NETWORK:
				switch (myapp.getShowmenu()) {
				case MENU_NORMAL:
					menu01_h_adddir.setVisibility(View.GONE);
					menu01_h_exit.setVisibility(View.GONE);
					menu01_h_addnetwork.setVisibility(View.GONE);
					menu01_h_back.setVisibility(View.VISIBLE);
					menu01_h_home.setVisibility(View.VISIBLE);
					menu01_h_find.setVisibility(View.VISIBLE);
					menu01_h_newfile.setVisibility(View.VISIBLE);
					menu01_h_copy.setVisibility(View.GONE);
					menu01_h_cut.setVisibility(View.GONE);
					menu01_h_paset.setVisibility(View.GONE);
					menu01_h_rename.setVisibility(View.GONE);
					menu01_h_del.setVisibility(View.GONE);
					switch (myapp.getShowwhat()) {
					case SHOW_LOCAL:
						menu01_h_share.setVisibility(View.VISIBLE);
						break;
					case SHOW_NETWORK:
						menu01_h_share.setVisibility(View.GONE);
						break;
					}
					menu01_h_all.setVisibility(View.GONE);
					menu01_h_cancel.setVisibility(View.GONE);
					menu01_h_view.setVisibility(View.VISIBLE);
					break;
				case MENU_SELECT:
					menu01_h_adddir.setVisibility(View.GONE);
					menu01_h_exit.setVisibility(View.GONE);
					menu01_h_addnetwork.setVisibility(View.GONE);
					menu01_h_back.setVisibility(View.GONE);
					menu01_h_home.setVisibility(View.GONE);
					menu01_h_find.setVisibility(View.GONE);
					menu01_h_newfile.setVisibility(View.GONE);
					switch (myapp.getShowwhat()) {
					case SHOW_LOCAL:
						menu01_h_copy.setVisibility(View.GONE);
						menu01_h_cut.setVisibility(View.VISIBLE);
						break;
					case SHOW_NETWORK:
						menu01_h_copy.setVisibility(View.VISIBLE);
						menu01_h_cut.setVisibility(View.GONE);
						break;
					}
					menu01_h_paset.setVisibility(View.GONE);
					menu01_h_rename.setVisibility(View.VISIBLE);
					menu01_h_del.setVisibility(View.VISIBLE);
					menu01_h_share.setVisibility(View.GONE);
					menu01_h_all.setVisibility(View.VISIBLE);
					menu01_h_cancel.setVisibility(View.VISIBLE);
					menu01_h_view.setVisibility(View.GONE);
					break;
				case MENU_COPY:
					menu01_h_adddir.setVisibility(View.GONE);
					menu01_h_exit.setVisibility(View.GONE);
					menu01_h_addnetwork.setVisibility(View.GONE);
					menu01_h_back.setVisibility(View.VISIBLE);
					menu01_h_home.setVisibility(View.VISIBLE);
					menu01_h_find.setVisibility(View.GONE);
					menu01_h_newfile.setVisibility(View.VISIBLE);
					menu01_h_copy.setVisibility(View.GONE);
					menu01_h_cut.setVisibility(View.GONE);
					menu01_h_paset.setVisibility(View.VISIBLE);
					menu01_h_rename.setVisibility(View.GONE);
					menu01_h_del.setVisibility(View.GONE);
					menu01_h_share.setVisibility(View.GONE);
					menu01_h_all.setVisibility(View.GONE);
					menu01_h_cancel.setVisibility(View.VISIBLE);
					menu01_h_view.setVisibility(View.GONE);
					break;
				}
				break;
			}
		} else {
			horizontal_menu.setVisibility(View.GONE);
			vertical_menu.setVisibility(View.VISIBLE);
			switch (myapp.getShowwhat()) {
			case SHOW_HOME:
				menu01_v_adddir.setVisibility(View.VISIBLE);
				menu01_v_exit.setVisibility(View.VISIBLE);
				menu01_v_addnetwork.setVisibility(View.VISIBLE);
				menu01_v_back.setVisibility(View.GONE);
				menu01_v_home.setVisibility(View.GONE);
				menu01_v_find.setVisibility(View.GONE);
				menu01_v_newfile.setVisibility(View.GONE);
				menu01_v_copy.setVisibility(View.GONE);
				menu01_v_cut.setVisibility(View.GONE);
				menu01_v_paset.setVisibility(View.GONE);
				menu01_v_rename.setVisibility(View.GONE);
				menu01_v_del.setVisibility(View.GONE);
				menu01_v_share.setVisibility(View.GONE);
				menu01_v_all.setVisibility(View.GONE);
				menu01_v_cancel.setVisibility(View.GONE);
				menu01_v_view.setVisibility(View.GONE);
				break;
			case SHOW_LOCAL:
			case SHOW_NETWORK:
				switch (myapp.getShowmenu()) {
				case MENU_NORMAL:
					menu01_v_adddir.setVisibility(View.GONE);
					menu01_v_exit.setVisibility(View.GONE);
					menu01_v_addnetwork.setVisibility(View.GONE);
					menu01_v_back.setVisibility(View.VISIBLE);
					menu01_v_home.setVisibility(View.VISIBLE);
					menu01_v_find.setVisibility(View.VISIBLE);
					menu01_v_newfile.setVisibility(View.VISIBLE);
					menu01_v_copy.setVisibility(View.GONE);
					menu01_v_cut.setVisibility(View.GONE);
					menu01_v_paset.setVisibility(View.GONE);
					menu01_v_rename.setVisibility(View.GONE);
					menu01_v_del.setVisibility(View.GONE);
					switch (myapp.getShowwhat()) {
					case SHOW_LOCAL:
						menu01_v_share.setVisibility(View.VISIBLE);
						break;
					case SHOW_NETWORK:
						menu01_v_share.setVisibility(View.GONE);
						break;
					}
					menu01_v_all.setVisibility(View.GONE);
					menu01_v_cancel.setVisibility(View.GONE);
					menu01_v_view.setVisibility(View.VISIBLE);
					break;
				case MENU_SELECT:
					menu01_v_adddir.setVisibility(View.GONE);
					menu01_v_exit.setVisibility(View.GONE);
					menu01_v_addnetwork.setVisibility(View.GONE);
					menu01_v_back.setVisibility(View.GONE);
					menu01_v_home.setVisibility(View.GONE);
					menu01_v_find.setVisibility(View.GONE);
					menu01_v_newfile.setVisibility(View.GONE);
					switch (myapp.getShowwhat()) {
					case SHOW_LOCAL:
						menu01_v_copy.setVisibility(View.GONE);
						menu01_v_cut.setVisibility(View.VISIBLE);
						break;
					case SHOW_NETWORK:
						menu01_v_copy.setVisibility(View.VISIBLE);
						menu01_v_cut.setVisibility(View.GONE);
						break;
					}
					menu01_v_paset.setVisibility(View.GONE);
					menu01_v_rename.setVisibility(View.VISIBLE);
					menu01_v_del.setVisibility(View.VISIBLE);
					menu01_v_share.setVisibility(View.GONE);
					menu01_v_all.setVisibility(View.VISIBLE);
					menu01_v_cancel.setVisibility(View.VISIBLE);
					menu01_v_view.setVisibility(View.GONE);
					break;
				case MENU_COPY:
					menu01_v_adddir.setVisibility(View.GONE);
					menu01_v_exit.setVisibility(View.GONE);
					menu01_v_addnetwork.setVisibility(View.GONE);
					menu01_v_back.setVisibility(View.VISIBLE);
					menu01_v_home.setVisibility(View.VISIBLE);
					menu01_v_find.setVisibility(View.GONE);
					menu01_v_newfile.setVisibility(View.VISIBLE);
					menu01_v_copy.setVisibility(View.GONE);
					menu01_v_cut.setVisibility(View.GONE);
					menu01_v_paset.setVisibility(View.VISIBLE);
					menu01_v_rename.setVisibility(View.GONE);
					menu01_v_del.setVisibility(View.GONE);
					menu01_v_share.setVisibility(View.GONE);
					menu01_v_all.setVisibility(View.GONE);
					menu01_v_cancel.setVisibility(View.VISIBLE);
					menu01_v_view.setVisibility(View.GONE);
					break;
				}
				break;
			}
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler MainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MAIN_HANDLER_NONET:
				progressdialog.cancel();
				break;
			case MAIN_HANDLER_NETOK:
				progressdialog.cancel();
				ShowItem("HOME");
				break;
			case MAIN_HANDLER_DELOK:
				Toast.makeText(MainActivity.this, "文件删除成功", Toast.LENGTH_SHORT);
				progressdialog.cancel();
				CancelSelect();
				ShowItem(myapp.getShow());
				break;
			case MAIN_HANDLER_DLGLOGIN:
				LayoutInflater factory = LayoutInflater.from(MainActivity.this);
				final View dlglongin = factory
						.inflate(R.layout.dlg_login, null);
				final EditText et1 = (EditText) dlglongin
						.findViewById(R.id.login_username);
				final EditText et2 = (EditText) dlglongin
						.findViewById(R.id.login_password);
				et1.setText(myapp.getUsername());
				et2.setText(myapp.getPassword());
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("输入登陆用户和密码：");
				builder.setView(dlglongin);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								myapp.setUsername(et1.getText().toString());
								myapp.setPassword(et2.getText().toString());
								ShowItem(myapp.getShow());
								dialog.cancel();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				builder.show();
				break;
			}
		}

	};

	private void UpAdapter() {
		switch (myapp.getShowview()) {
		case VIEW_LIST1:
			if (myapp.getShowwhat() == SHOW_HOME) {
				gridadapter.notifyDataSetChanged();
			} else {
				listadapter.notifyDataSetChanged();
			}
			break;
		case VIEW_GRID4:
		case VIEW_GRID5:
		case VIEW_GRID6:
			gridadapter.notifyDataSetChanged();
			break;
		default:
			listadapter.notifyDataSetChanged();
			gridadapter.notifyDataSetChanged();
			break;
		}
	}

	private class ListLocalFileTask extends
			AsyncTask<String, HashMap<String, Object>, Void> {
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params) {
			File file = new File(params[0]);
			File listf[] = file.listFiles();
			if (listf != null) {
				for (File f : listf) {
					if ((f.getName().charAt(0)) == '.')
						continue;
					HashMap<String, Object> map = new HashMap<String, Object>();
					/* 根据文件类型确定图标 */
					map.put("NAME", f.getName());
					long time = f.lastModified();
					String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
							Locale.getDefault()).format(new Date(time));
					if (f.isDirectory()) {
						map.put("MARK", TYPE_DIR2);
						map.put("INFO", "Date:" + ctime);
					} else {
						map.put("MARK", TYPE_FILE);
						long filesize = f.length();
						String strfilesize;
						if ((filesize >> 20) > 0) {
							strfilesize = (new DecimalFormat(".00")
									.format((float) filesize / 1024 / 1024))
									+ "M";
						} else if ((filesize >> 10) > 0) {
							strfilesize = (new DecimalFormat(".00")
									.format((float) filesize / 1024)) + "KB";
						} else {
							strfilesize = filesize + "Byte";
						}
						map.put("INFO", "Date:" + ctime + "  Size:"
								+ strfilesize);
					}
					map.put("PATH", f.getAbsolutePath());
					publishProgress(map);
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			myapp.getList().clear();
			myapp.getUpIamge_list().clear();
			UpAdapter();
			progressdialog.setMessage("正在加载...");
			progressdialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Collections.sort(myapp.getList(),
					new Comparator<Map<String, Object>>() {
						@Override
						public int compare(Map<String, Object> map1,
								Map<String, Object> map2) {
							if ((Integer) map1.get("MARK") > (Integer) map2
									.get("MARK")) {
								return 1;
							} else if ((Integer) map1.get("MARK") < (Integer) map2
									.get("MARK")) {
								return -1;
							} else if (((String) map1.get("NAME"))
									.compareToIgnoreCase((String) map2
											.get("NAME")) > 0) {
								return 1;
							} else if (((String) map1.get("NAME"))
									.compareToIgnoreCase((String) map2
											.get("NAME")) < 0) {
								return -1;
							} else {
								return 0;
							}
						}
					});
			UpAdapter();
			progressdialog.cancel();
		}

		@Override
		protected void onProgressUpdate(HashMap<String, Object>... map) {
			myapp.getList().add(map[0]);
			// UpAdapter();
			super.onProgressUpdate(map);
		}

	}

	private class ListNetworkFileTask extends
			AsyncTask<String, HashMap<String, Object>, Void> {
		@Override
		protected void onPreExecute() {
			myapp.getList().clear();
			myapp.getUpIamge_list().clear();
			UpAdapter();
			progressdialog.setMessage("正在加载...");
			progressdialog.show();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params) {
			try {
				NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
						"", myapp.getUsername(), myapp.getPassword());
				SmbFile smbfile = new SmbFile(myapp.getShow(), auth);
				ArrayList<SmbFile> dirList = new ArrayList<SmbFile>();
				ArrayList<SmbFile> fileList = new ArrayList<SmbFile>();
				SmbFile[] fs = smbfile.listFiles();
				for (SmbFile f : fs) {
					if (f.isDirectory()) {
						dirList.add(f);
					} else {
						fileList.add(f);
					}
				}
				dirList.addAll(fileList);
				for (SmbFile f : dirList) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					long time = f.lastModified();
					String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
							Locale.getDefault()).format(new Date(time));
					map.put("PATH", f.getPath());
					if (f.isDirectory()) {
						map.put("MARK", TYPE_DIR2_NET);
						map.put("INFO", "Date:" + ctime);
						map.put("NAME",
								f.getName().substring(0,
										f.getName().length() - 1));
					} else {
						map.put("MARK", TYPE_FILE);
						long filesize = f.length();
						String strfilesize;
						if ((filesize >> 20) > 0) {
							strfilesize = (new DecimalFormat(".00")
									.format((float) filesize / 1024 / 1024))
									+ "M";
						} else if ((filesize >> 10) > 0) {
							strfilesize = (new DecimalFormat(".00")
									.format((float) filesize / 1024)) + "KB";
						} else {
							strfilesize = filesize + "Byte";
						}
						map.put("INFO", "Date:" + ctime + "  Size:"
								+ strfilesize);
						map.put("NAME",
								f.getName().substring(0, f.getName().length()));
					}
					publishProgress(map);
				}
			} catch (SmbAuthException e) {
				Message msg = new Message();
				msg.what = MAIN_HANDLER_DLGLOGIN;
				MainHandler.sendMessage(msg);
			} catch (SmbException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			UpAdapter();
			progressdialog.cancel();
		}

		@Override
		protected void onProgressUpdate(HashMap<String, Object>... values) {
			myapp.getList().add(values[0]);
			super.onProgressUpdate(values);
		}

	}

	// private class ListFindNetWorkTask extends
	// AsyncTask<String, HashMap<String, Object>, Void> {
	// @Override
	// protected void onPreExecute() {
	// progressdialog.setMessage("正在测试连接...");
	// progressdialog.show();
	// }
	//
	// @Override
	// protected Void doInBackground(String... params) {
	//
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// super.onPostExecute(result);
	// UpAdapter();
	// progressdialog.cancel();
	// }
	//
	// @Override
	// protected void onProgressUpdate(HashMap<String, Object>... values) {
	// myapp.getList().add(values[0]);
	// super.onProgressUpdate(values);
	// }
	//
	// }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String PATH = (String) myapp.getList().get((int) arg3).get("PATH");
		switch (myapp.getShowwhat()) {
		case SHOW_HOME:
			if ((Integer) myapp.getList().get((int) arg3).get("MARK") == TYPE_DIR1_NET) {
				myapp.setUsername((String) myapp.getList().get((int) arg3)
						.get("USER"));
				myapp.setPassword((String) myapp.getList().get((int) arg3)
						.get("PASS"));
			}
			myapp.getPathstack().push(myapp.getShow());
			myapp.setShow(PATH);
			ShowItem(myapp.getShow());
			break;
		case SHOW_LOCAL:
			switch (myapp.getShowmenu()) {
			case MENU_SELECT:
				UPCheckItem((int) arg3);
				SetViewVisible();
				UpAdapter();
				break;
			case MENU_COPY:
			case MENU_NORMAL:
				File f = new File(PATH);
				if (f.isDirectory()) {
					myapp.getPathstack().push(myapp.getShow());
					myapp.setShow(PATH);
					ShowItem(myapp.getShow());
				} else {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					String type = f.getName().substring(
							(f.getName()).lastIndexOf(".") + 1);
					type = FileMIME.getMIMEType(f);
					intent.setDataAndType(Uri.fromFile(f), type);
					startActivity(intent);
				}
				break;
			}
			break;
		case SHOW_NETWORK:
			switch (myapp.getShowmenu()) {
			case MENU_SELECT:
				UPCheckItem((int) arg3);
				SetViewVisible();
				UpAdapter();
				break;
			case MENU_COPY:
			case MENU_NORMAL:
				if (PATH.lastIndexOf("/") == PATH.length() - 1) {
					myapp.getPathstack().push(myapp.getShow());
					myapp.setShow((String) myapp.getList().get((int) arg3)
							.get("PATH"));
					ShowItem(myapp.getShow());
				} else {
					// 如果是文件，待加入处理方法
				}
				break;
			}
			break;
		}
	}

	private void UPCheckItem(int position) {
		if (selectList == null) {
			selectList = new ArrayList<HashMap<String, Object>>();
		}
		int type = (Integer) myapp.getList().get(position).get("MARK");
		if (type % 2 == 1) {
			myapp.getList().get(position).put("MARK", type - 1);
			if (selectList.size() > 0) {
				int i = 0;
				String name = (String) myapp.getList().get(position)
						.get("NAME");
				while (false == (name.equals(selectList.get(i).get("NAME")))) {
					i++;
				}
				selectList.remove(i);
			}
			if (selectList.isEmpty()) {
				myapp.setShowmenu(MENU_NORMAL);
				SetViewVisible();
			}
			// Log.i(TAG, selectList.toString());
		} else {
			myapp.getList().get(position).put("MARK", type + 1);
			selectList.add(myapp.getList().get(position));
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		switch ((Integer) myapp.getList().get((int) id).get("MARK")) {
		case TYPE_ROOT:
		case TYPE_SDCARD:
			break;
		case TYPE_DIR1:
			M_EditDirItem((int) id);
			break;
		case TYPE_DIR1_NET:
			M_EditNetItem((int) id);
			break;
		default:
			switch (myapp.getShowmenu()) {
			case MENU_NORMAL:
				CancelSelect();
				myapp.setShowmenu(MENU_SELECT);
				UPCheckItem((int) id);
				SetViewVisible();
				UpAdapter();
				break;
			case MENU_SELECT:
				UPCheckItem((int) id);
				UpAdapter();
				break;
			case MENU_COPY:
				break;
			}
		}
		return true;
	}

	private void M_EditNetItem(int position) {
		LayoutInflater lf = LayoutInflater.from(MainActivity.this);
		final View view = lf.inflate(R.layout.dlg_addnet, null);
		final EditText et1 = (EditText) view.findViewById(R.id.adnetwork_et01);
		final EditText et2 = (EditText) view.findViewById(R.id.adnetwork_et02);
		final EditText et3 = (EditText) view.findViewById(R.id.adnetwork_et03);
		final EditText et4 = (EditText) view.findViewById(R.id.adnetwork_et04);
		final String mPath = (String) myapp.getList().get(position).get("PATH");
		et1.setText(mPath.substring(6, mPath.length() - 1));
		et2.setText((CharSequence) myapp.getList().get(position).get("USER"));
		et3.setText((CharSequence) myapp.getList().get(position).get("PASS"));
		et4.setText((CharSequence) myapp.getList().get(position).get("NAME"));
		AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
		dlg.setTitle("快捷选项信息");
		dlg.setView(view);
		dlg.setPositiveButton("修改", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressdialog.setMessage("正在测试连接...");
				progressdialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String path = "smb://" + et1.getText().toString()
									+ "/";
							String user = et2.getText().toString();
							String pass = et3.getText().toString();
							String name = et4.getText().toString();
							NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
									"", user, pass);
							SmbFile smbfile = new SmbFile(path, auth);
							SmbFile[] fs = smbfile.listFiles();
							// 判断是否可以打开
							if (fs != null) {
								ContentValues cv = new ContentValues();
								cv.put("PATH", path);
								if (name.equals("")) {
									name = path;
								}
								cv.put("NAME", name);
								cv.put("USER", user);
								cv.put("PASS", pass);
								cv.put("MARK", TYPE_DIR1_NET);
								DBhelper mydb = new DBhelper(MainActivity.this);
								mydb.Update(cv, "PATH=?", new String[] { path });
								mydb.close();
								Message msg = new Message();
								msg.what = MAIN_HANDLER_NETOK;
								MainHandler.sendMessage(msg);
							} else {
								// 如果打开的网上电脑没不能显示
								Message msg = new Message();
								msg.what = MAIN_HANDLER_NONET;
								MainHandler.sendMessage(msg);
							}
						} catch (MalformedURLException e) {
							// 如果打开的网上电脑没不能显示
							Message msg = new Message();
							msg.what = MAIN_HANDLER_NONET;
							MainHandler.sendMessage(msg);
							e.printStackTrace();
						} catch (SmbException e) {
							// 如果打开的网上电脑没不能显示
							Message msg = new Message();
							msg.what = MAIN_HANDLER_NONET;
							MainHandler.sendMessage(msg);
							e.printStackTrace();
						}
					}
				}).start();
				dialog.cancel();
			}
		});
		dlg.setNeutralButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DBhelper mydb = new DBhelper(MainActivity.this);
				mydb.DeleteOne(mPath);
				mydb.close();
				ShowItem("HOME");
				dialog.cancel();
			}
		});
		dlg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dlg.show();

	}

	private void M_EditDirItem(int position) {
		LayoutInflater lfdlgadddir = LayoutInflater.from(MainActivity.this);
		final View vdlgadddir = lfdlgadddir.inflate(R.layout.dlg_adddir, null);
		final EditText dlgadddir_et1 = (EditText) vdlgadddir
				.findViewById(R.id.dlgadditem_et01);
		final EditText dlgadddir_et2 = (EditText) vdlgadddir
				.findViewById(R.id.dlgadditem_et02);
		final String mPath = (String) myapp.getList().get(position).get("PATH");
		dlgadddir_et1.setText(mPath);
		dlgadddir_et2.setText((String) myapp.getList().get(position)
				.get("NAME"));
		AlertDialog.Builder dlgadddir = new AlertDialog.Builder(
				MainActivity.this);
		dlgadddir.setTitle("快捷选项信息");
		dlgadddir.setView(vdlgadddir);
		dlgadddir.setPositiveButton("修改",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String path = dlgadddir_et1.getText().toString();
						String name = dlgadddir_et2.getText().toString();
						File f = new File(path);
						if (f.exists()) {
							ContentValues cv = new ContentValues();
							cv.put("PATH", path);
							if (name.equals("")) {
								name = f.getName();
							}
							cv.put("NAME", name);
							cv.put("MARK", TYPE_DIR1);
							DBhelper mydb = new DBhelper(MainActivity.this);
							mydb.Update(cv, "PATH=?", new String[] { path });
							mydb.close();
							ShowItem("HOME");
						}
						dialog.cancel();
					}
				});
		dlgadddir.setNeutralButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DBhelper mydb = new DBhelper(MainActivity.this);
				mydb.DeleteOne(mPath);
				mydb.close();
				ShowItem("HOME");
				dialog.cancel();
			}
		});
		dlgadddir.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		dlgadddir.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 全选
		case R.id.menu01_v_all:
		case R.id.menu01_h_all:
			for (HashMap<String, Object> map : myapp.getList()) {
				int TYPE = (Integer) map.get("MARK");
				if (TYPE % 2 == 0) {
					map.put("MARK", TYPE + 1);
					selectList.add(map);
				}
			}
			UpAdapter();
			break;
		// 新目录
		case R.id.menu01_v_adddir:
		case R.id.menu01_h_adddir:
			M_AddDir();
			break;

		// 局域网
		case R.id.menu01_v_addnetwork:
		case R.id.menu01_h_addnetwork:
			M_AddNet();
			break;

		// 粘贴
		case R.id.menu01_v_paset:
		case R.id.menu01_h_paset:
			M_Paset();
			break;

		// 复制
		case R.id.menu01_v_copy:
		case R.id.menu01_h_copy:
			M_Copy();
			break;

		// 移动 剪切
		case R.id.menu01_h_cut:
		case R.id.menu01_v_cut:
			M_Cut();
			break;

		// 删除
		case R.id.menu01_v_del:
		case R.id.menu01_h_del:
			M_Del();
			break;

		// 新建
		case R.id.menu01_v_newfile:
		case R.id.menu01_h_newfile:
			M_NewFile();
			break;

		// 重命名:
		case R.id.menu01_v_rename:
		case R.id.menu01_h_rename:
			M_ReName();
			break;

		// 取消
		case R.id.menu01_v_cancel:
		case R.id.menu01_h_cancel:
			CancelSelect();
			UpAdapter();
			break;

		// 首页
		case R.id.menu01_v_home:
		case R.id.menu01_h_home:
			// CancelSelect();
			myapp.getPathstack().clear();
			myapp.setShow("HOME");
			ShowItem(myapp.getShow());
			break;

		// 退出
		case R.id.menu01_v_exit:
		case R.id.menu01_h_exit:
			finish();
			break;

		// 后退
		case R.id.menu01_v_back:
		case R.id.menu01_h_back:
			if (myapp.getPathstack().isEmpty() == false) {
				myapp.setShow(myapp.getPathstack().pop());
				ShowItem(myapp.getShow());
			}
			break;
		// 视图
		case R.id.menu01_v_view:
		case R.id.menu01_h_view:
			if (myapp.getShowview() >= 4) {
				myapp.setShowview(1);
			} else {
				myapp.setShowview(myapp.getShowview() + 1);
			}
			SetViewVisible();
			UpAdapter();
			break;

		// 共享
		case R.id.menu01_v_share:
		case R.id.menu01_h_share:
			M_Share();
			break;
		// 快捷
		case R.id.menu01_v_find:
		case R.id.menu01_h_find:
			M_Find();
			break;
		}
	}

	private void M_Find() {
		switch (myapp.getShowwhat()) {
		case SHOW_LOCAL:
			String path = myapp.getShow();
			ContentValues cv = new ContentValues();
			cv.put("PATH",path );
			int i = path.lastIndexOf("/")+1;
			cv.put("NAME", path.substring(i));
			cv.put("MARK", TYPE_DIR1);
			DBhelper mydb = new DBhelper(MainActivity.this);
			mydb.Insert(cv);
			mydb.close();
			break;		
		}

	}

	private void M_Share() {
		if (mFtpConfigfile != null) {
			mFtpConfigfile.delete();
			mFtpConfigfile = null;
		}
		if (mFtpServer != null) {
			mFtpServer.stop();
			mFtpServer = null;
		}
		String configPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + ".FtpConfig";
		mFtpConfigfile = new File(configPath);
		mFtpServer = FtpTools.startFtpServer(myapp.getShow(), mFtpConfigfile,
				ftpPort, "utf-8");
		try {
			mFtpServer.start();
			AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
			dlg.setTitle(myapp.getShow() + " 已共享!");
			dlg.setMessage("ftp://" + MobilePC.getHostIP() + ":" + ftpPort);
			dlg.setNegativeButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			dlg.show();
		} catch (FtpException e) {
			e.printStackTrace();
		}

	}

	private void M_AddNet() {
		LayoutInflater lf = LayoutInflater.from(MainActivity.this);
		final View view = lf.inflate(R.layout.dlg_addnet, null);
		final EditText et1 = (EditText) view.findViewById(R.id.adnetwork_et01);
		final EditText et2 = (EditText) view.findViewById(R.id.adnetwork_et02);
		final EditText et3 = (EditText) view.findViewById(R.id.adnetwork_et03);
		final EditText et4 = (EditText) view.findViewById(R.id.adnetwork_et04);
		@SuppressWarnings("static-access")
		String IP = new MobilePC().getHostIP();
		et1.setText(IP.substring(0, IP.lastIndexOf(".") + 1));
		et2.setText("GUEST");
		AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
		dlg.setTitle("添加网络选项");
		dlg.setView(view);
		dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressdialog.setMessage("正在测试连接...");
				progressdialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						String IP = null;
						try {
							try {
								InetAddress inet = InetAddress.getByName(et1
										.getText().toString());
								IP = inet.getHostAddress();
							} catch (UnknownHostException e) {
								IP = et1.getText().toString();
								e.printStackTrace();
							}
							String path = "smb://" + IP + "/";
							Log.i("LOGTEST", path);
							String user = et2.getText().toString();
							String pass = et3.getText().toString();
							String name = et4.getText().toString();
							NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
									"", user, pass);
							SmbFile smbfile = new SmbFile(path, auth);
							SmbFile[] fs = smbfile.listFiles();
							// 判断是否可以打开
							if (fs != null) {
								ContentValues cv = new ContentValues();
								cv.put("PATH", path);
								if (name.equals("")) {
									name = et1.getText().toString();
								}
								cv.put("NAME", name);
								cv.put("USER", user);
								cv.put("PASS", pass);
								cv.put("MARK", TYPE_DIR1_NET);
								DBhelper mydb = new DBhelper(MainActivity.this);
								mydb.Insert(cv);
								mydb.close();
								Message msg = new Message();
								msg.what = MAIN_HANDLER_NETOK;
								MainHandler.sendMessage(msg);
							} else {
								// 如果打开的网上电脑没不能显示
								Message msg = new Message();
								msg.what = MAIN_HANDLER_NONET;
								MainHandler.sendMessage(msg);
							}
						} catch (MalformedURLException e) {
							// 如果打开的网上电脑没不能显示
							Message msg = new Message();
							msg.what = MAIN_HANDLER_NONET;
							MainHandler.sendMessage(msg);
							e.printStackTrace();
						} catch (SmbException e) {
							// 如果打开的网上电脑没不能显示
							Message msg = new Message();
							msg.what = MAIN_HANDLER_NONET;
							MainHandler.sendMessage(msg);
							e.printStackTrace();
						}
					}
				}).start();
				dialog.cancel();
			}
		});
		dlg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dlg.show();
	}

	private void M_Cut() {
		switch (myapp.getShowwhat()) {
		case SHOW_LOCAL:
			switch (myapp.getCopymode()) {
			case MODE_NONECOPY:
			case MODE_LOCCOPY:
			case MODE_LOCCUT:
			case MODE_NETCOPY:
			case MODE_NETCUT:
				if (selectList.size() > 0) {
					myapp.getCopylist().clear();
					for (HashMap<String, Object> map : selectList) {
						myapp.getCopylist().add((String) map.get("PATH"));
					}
					Log.i("TESTLOG", myapp.getCopylist().toString());
					myapp.setCopymode(MODE_LOCCUT);
					myapp.setShowmenu(MENU_COPY);
					SetViewVisible();
				}
				break;
			case MODE_ISCOPY:
				Toast.makeText(MainActivity.this, "正在复制文件,请稍候...",
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		case SHOW_NETWORK:
			switch (myapp.getCopymode()) {
			case MODE_NONECOPY:
			case MODE_LOCCOPY:
			case MODE_LOCCUT:
			case MODE_NETCOPY:
			case MODE_NETCUT:
				if (selectList.size() > 0) {
					for (HashMap<String, Object> map : selectList) {
						myapp.getCopylist().clear();
						myapp.getCopylist().add((String) map.get("PATH"));
					}
					myapp.setCopymode(MODE_NETCUT);
					myapp.setShowmenu(MENU_COPY);
					SetViewVisible();
				}
				break;
			case MODE_ISCOPY:
				Toast.makeText(MainActivity.this, "正在复制文件,请稍候...",
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		}
	}

	private void M_Copy() {
		switch (myapp.getShowwhat()) {
		case SHOW_LOCAL:
			switch (myapp.getCopymode()) {
			case MODE_NONECOPY:
				// if (selectList.size() > 0) {
				// for (HashMap<String, Object> map : selectList) {
				// myapp.getCopylist().add((String) map.get("PATH"));
				// }
				// myapp.setCopymode(MODE_LOCCOPY);
				// myapp.setShowmenu(MENU_COPY);
				// SetControlPosition();
				// }
				// break;
			case MODE_LOCCOPY:
			case MODE_LOCCUT:
			case MODE_NETCOPY:
			case MODE_NETCUT:
				if (selectList.size() > 0) {
					myapp.getCopylist().clear();
					for (HashMap<String, Object> map : selectList) {
						myapp.getCopylist().add((String) map.get("PATH"));
					}
					myapp.setCopymode(MODE_LOCCOPY);
					myapp.setShowmenu(MENU_COPY);
					SetViewVisible();
				}
				break;
			case MODE_ISCOPY:
				Toast.makeText(MainActivity.this, "正在复制文件,请稍候...",
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		case SHOW_NETWORK:
			switch (myapp.getCopymode()) {
			case MODE_NONECOPY:
				// if (selectList.size() > 0) {
				// for (HashMap<String, Object> map : selectList) {
				// myapp.getCopylist().add((String) map.get("PATH"));
				// }
				// myapp.setCopymode(MODE_NETCOPY);
				// myapp.setShowmenu(MENU_COPY);
				// SetControlPosition();
				// }
				// break;
			case MODE_LOCCOPY:
			case MODE_LOCCUT:
			case MODE_NETCOPY:
			case MODE_NETCUT:
				if (selectList.size() > 0) {
					myapp.getCopylist().clear();
					for (HashMap<String, Object> map : selectList) {
						myapp.getCopylist().add((String) map.get("PATH"));
					}
					myapp.setCopymode(MODE_NETCOPY);
					myapp.setShowmenu(MENU_COPY);
					SetViewVisible();
				}
				break;
			case MODE_ISCOPY:
				Toast.makeText(MainActivity.this, "正在复制文件,请稍候...",
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		}
	}

	private void M_Del() {
		switch (myapp.getShowwhat()) {
		case SHOW_LOCAL:
			if (selectList.size() > 0) {
				for (HashMap<String, Object> map : selectList) {
					myapp.getDellist().add((String) map.get("PATH"));
				}
				AlertDialog.Builder dlgdel = new AlertDialog.Builder(
						MainActivity.this);
				dlgdel.setTitle("删除文件");
				dlgdel.setMessage("删除(" + myapp.getDellist().get(0) + ")等"
						+ myapp.getDellist().size() + "项...");
				dlgdel.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								progressdialog.setMessage("正在删除...");
								progressdialog.show();
								new Thread(new Runnable() {
									@Override
									public void run() {
										for (String path : myapp.getDellist()) {
											FileDelete(new File(path));
										}
										Message msg = new Message();
										msg.what = MAIN_HANDLER_DELOK;
										MainHandler.sendMessage(msg);
										myapp.getDellist().clear();
									}
								}).start();
								dialog.cancel();
							}
						});
				dlgdel.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								myapp.getDellist().clear();
								dialog.cancel();
							}
						});
				dlgdel.show();
			}
			break;
		case SHOW_NETWORK:
			// 网络删除文件尚未开发
			break;
		}
	}

	private void M_Paset() {
		switch (myapp.getShowwhat()) {
		case SHOW_LOCAL:
			myapp.setCopytopath(myapp.getShow());
			if (myapp.getCopylist().size() > 0) {
				Intent intent = new Intent(this, FileCopyService.class);
				startService(intent);
				CancelSelect();
				dlg_copyfile_isshow = true;
				ShowDlgCopyFile();
			}
			break;
		case SHOW_NETWORK:
			// 网络粘贴还未开发
			break;
		}
	}

	private void M_AddDir() {
		LayoutInflater lfdlgadddir = LayoutInflater.from(MainActivity.this);
		final View vdlgadddir = lfdlgadddir.inflate(R.layout.dlg_adddir, null);
		final EditText dlgadddir_et1 = (EditText) vdlgadddir
				.findViewById(R.id.dlgadditem_et01);
		final EditText dlgadddir_et2 = (EditText) vdlgadddir
				.findViewById(R.id.dlgadditem_et02);
		AlertDialog.Builder dlgadddir = new AlertDialog.Builder(
				MainActivity.this);
		dlgadddir.setTitle("添加目录选项");
		dlgadddir.setView(vdlgadddir);
		dlgadddir.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String PATH = dlgadddir_et1.getText().toString();
						String NAME = dlgadddir_et2.getText().toString();
						File f = new File(PATH);
						if (f.exists()) {
							ContentValues cv = new ContentValues();
							cv.put("PATH", PATH);
							if (NAME.equals("")) {
								NAME = f.getName();
							}
							cv.put("NAME", NAME);
							cv.put("MARK", TYPE_DIR1);
							DBhelper mydb = new DBhelper(MainActivity.this);
							mydb.Insert(cv);
							mydb.close();
							ShowItem("HOME");
						}
						dialog.cancel();
					}
				});
		dlgadddir.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		dlgadddir.show();
	}

	private void M_NewFile() {
		switch (myapp.getShowwhat()) {
		case SHOW_LOCAL:
			LayoutInflater factory2 = LayoutInflater.from(MainActivity.this);
			View dlgnewfile = factory2.inflate(R.layout.dlg_newfile, null);
			final EditText et1 = (EditText) dlgnewfile
					.findViewById(R.id.dlgnewfile_et01);
			final RadioGroup rg1 = (RadioGroup) dlgnewfile
					.findViewById(R.id.dlgnewfile_rg01);
			int num = 1;
			String newfile = "新建文件";
			for (int i = 0; i < num; i++) {
				for (HashMap<String, Object> map : myapp.getList()) {
					if (((String) map.get("NAME")).equals(newfile + num)) {
						num++;
					}
				}
			}
			et1.setText(newfile + num);
			AlertDialog.Builder dlgrename = new AlertDialog.Builder(
					MainActivity.this);
			dlgrename.setTitle("新建文件");
			dlgrename.setView(dlgnewfile);
			dlgrename.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							String newpath = myapp.getShow() + "/"
									+ et1.getText();
							File f = new File(newpath);
							if (f.exists()) {
								Toast.makeText(MainActivity.this,
										"文件名已存在,重新输入文件名", Toast.LENGTH_SHORT)
										.show();
							} else {
								boolean result = false;
								switch (rg1.getCheckedRadioButtonId()) {
								case R.id.dlgnewfile_rb01:
									result = f.mkdir();
									if (result) {
										map.put("MARK", TYPE_DIR2 + 1);
										Toast.makeText(MainActivity.this,
												"建立新文件夹成功", Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast.makeText(MainActivity.this,
												"建立新文件夹失败", Toast.LENGTH_SHORT)
												.show();
									}
									break;
								case R.id.dlgnewfile_rb02:
									try {
										result = f.createNewFile();
									} catch (IOException e) {
										e.printStackTrace();
									}
									if (result) {
										map.put("MARK", TYPE_FILE + 1);
										Toast.makeText(MainActivity.this,
												"建立新文件成功", Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast.makeText(MainActivity.this,
												"建立新文件失败", Toast.LENGTH_SHORT)
												.show();
									}
									break;
								}
								if (result) {
									map.put("PATH", f.getAbsolutePath());
									map.put("NAME", f.getName());
									map.put("INFO", "");
									myapp.getList().add(map);
									UpAdapter();
									switch (myapp.getShowview()) {
									case VIEW_LIST1:
										main_lv01.setSelection(myapp.getList()
												.size() - 1);
										break;
									case VIEW_GRID4:
									case VIEW_GRID5:
									case VIEW_GRID6:
										main_gv01.setSelection(myapp.getList()
												.size() - 1);
										break;
									}
								}
							}
							dialog.cancel();
						}
					});
			dlgrename.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			dlgrename.show();
			break;
		case SHOW_NETWORK:
			break;
		}
	}

	private void M_ReName() {
		switch (myapp.getShowwhat()) {
		case SHOW_LOCAL:
			if (selectList.size() > 0) {
				LayoutInflater factory1 = LayoutInflater
						.from(MainActivity.this);
				final View dlgedit = factory1
						.inflate(R.layout.dlg_rename, null);
				final TextView tv1 = (TextView) dlgedit
						.findViewById(R.id.dlgrename_tv01);
				final EditText et1 = (EditText) dlgedit
						.findViewById(R.id.dlgrename_et01);
				final String rnname = (String) selectList.get(
						selectList.size() - 1).get("NAME");
				final String rnpath = (String) selectList.get(
						selectList.size() - 1).get("PATH");
				tv1.setText(Html.fromHtml("<font color=green>" + rnname
						+ "</font> 改名为："));
				et1.setText(rnname);
				AlertDialog.Builder dlgrename = new AlertDialog.Builder(
						MainActivity.this);
				dlgrename.setTitle("重命名");
				dlgrename.setView(dlgedit);
				dlgrename.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								boolean result = new File(rnpath)
										.renameTo(new File(rnpath.substring(0,
												rnpath.lastIndexOf("/") + 1)
												+ et1.getText()));
								if (result) {
									Toast.makeText(MainActivity.this, "重命名成功",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(MainActivity.this, "重命名失败",
											Toast.LENGTH_SHORT).show();
								}
								CancelSelect();
								ShowItem(myapp.getShow());
								dialog.cancel();
							}
						});
				dlgrename.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				dlgrename.show();
			}
			break;
		case SHOW_NETWORK:
			// 网络文件重命名尚未开发
			break;
		}
	}

	// private void AddNetWork(){
	// LayoutInflater factory2 = LayoutInflater.from(MainActivity.this);
	// final View dlglongin = factory2.inflate(R.layout.dlgadd_network, null);
	// final TextView tv2 = (TextView) dlglongin
	// .findViewById(R.id.connet_tv01);
	// final EditText et2 = (EditText) dlglongin
	// .findViewById(R.id.connet_et01);
	// tv2.setText("输入要连接的电脑的IP地址 :");
	// String IP = new MobilePC().getHostIP();
	// et2.setText(IP.substring(0, IP.lastIndexOf(".") + 1));
	// AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	// builder.setTitle("局域网连接");
	// builder.setView(dlglongin);
	// builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// myapp.setShowwhat(SHOW_NETWORK);
	// myapp.setShow("smb://" + et2.getText().toString() + "/");
	// ShowItem(myapp.getShow());
	// dialog.cancel();
	// }
	// });
	// builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.cancel();
	// }
	// });
	// builder.show();
	//
	// }

	private void CancelSelect() {
		myapp.setShowmenu(MENU_NORMAL);
		if (selectList != null) {
			for (HashMap<String, Object> map : selectList) {
				int TYPE = (Integer) map.get("MARK");
				map.put("MARK", TYPE - 1);
			}
			selectList.clear();
		}
		SetViewVisible();
	}

	@Override
	public void OnItemCheckBoxClick(View v) {
		int position = (Integer) v.getTag();
		switch (myapp.getShowmenu()) {
		case MENU_NORMAL:
			CancelSelect();
			myapp.setShowmenu(MENU_SELECT);
			UPCheckItem(position);
			SetViewVisible();
			UpAdapter();
			break;
		case MENU_SELECT:
			UPCheckItem(position);
			UpAdapter();
			break;
		}
	}

	// 删除文件和文件夹
	private void FileDelete(File f) {
		if (f.isDirectory()) {
			if (null != f.listFiles()) {
				for (File f1 : f.listFiles()) {
					FileDelete(f1);
				}
			}
			f.delete();
		} else if (f.isFile()) {
			f.delete();
		}
	}

	private void ShowText(String s) {
		if (s.indexOf("smb://") == 0) {
			s = "[网络]" + s.substring(5, s.length());
		} else if (s.indexOf("/") == 0) {
			s = "[本机]" + s;
		}
		main_tv01.setText(s);
	}

	private class MainBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MAIN_ACTION_BROADCAST_UPVIEW)) {
				if (myapp.getCopytopath().equals(myapp.getShow())) {
					ShowItem(myapp.getShow());
				}
			} else if (dlg_copyfile_isshow) {
				if (action.equals(MAIN_ACTION_BROADCAST_UPDLGCOPY_MAX)) {
					dlg_copyfile_sb01.setMax(FileCopyService.copymaxnum);
				} else if (action.equals(MAIN_ACTION_BROADCAST_UPDLGCOPY_PRO)) {
					dlg_copyfile_tv01.setText("从:"
							+ FileCopyService.copyfromfilename);
					dlg_copyfile_tv02.setText("到:"
							+ FileCopyService.copytofilename);
					dlg_copyfile_sb01.setProgress(FileCopyService.copyednum);
				} else if (action.equals(MAIN_ACTION_BROADCAST_UPDLGCOPY_END)) {
					dlg_copyfile_isshow = false;
					dlg_copyfile.dismiss();
				}
			} else if (action.equals(MAIN_ACTION_BROADCAST_UPIMAGE)) {
				MyApp.upImage_map_lock = true;
				if (myapp.getUpImage_map().isEmpty() == false) {
					// String path1 = (String)
					// myapp.getUpImage_map().get("PATH");
					Bitmap bm = (Bitmap) myapp.getUpImage_map().get("minbm");
					int position = (Integer) myapp.getUpImage_map().get(
							"POSITION");
					// String path2 = null;
					if (position < myapp.getList().size()) {
						// path2 = (String)
						// myapp.getList().get(position).get("PATH");
						// if (path1.equals(path2)) {
						// Log.i(MyApp.TAG, "path1=" + path1 + "path2="+ path2);
						myapp.getList().get(position).put("minbm", bm);
						gridadapter.notifyDataSetChanged();
						MyApp.upImage_map_lock = false;
						return;
						// }
					}
					MyApp.upImage_map_lock = false;

				}
			}
		}

	}

	private void ShowDlgCopyFile() {
		LayoutInflater lf = LayoutInflater.from(MainActivity.this);
		View view = lf.inflate(R.layout.dlg_copyfile, null);
		dlg_copyfile_tv01 = (TextView) view
				.findViewById(R.id.dlg_copyfile_tv01);
		dlg_copyfile_tv02 = (TextView) view
				.findViewById(R.id.dlg_copyfile_tv02);
		dlg_copyfile_sb01 = (SeekBar) view.findViewById(R.id.dlg_copyfile_sb01);
		AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
		switch (myapp.getCopymode()) {
		case MODE_NETCUT:
		case MODE_LOCCUT:
			dlg.setTitle("移动文件");
			break;
		case MODE_NETCOPY:
		case MODE_LOCCOPY:
			dlg.setTitle("复制文件");
			break;
		}
		if (FileCopyService.copytofilename.toString().equals("")) {
			dlg_copyfile_tv01.setText("正在统计文件数量....");
			dlg_copyfile_tv02.setText("");
		} else {
			dlg_copyfile_tv01.setText("从:" + FileCopyService.copyfromfilename);
			dlg_copyfile_tv02.setText("到:" + FileCopyService.copytofilename);
		}
		dlg_copyfile_sb01.setMax(FileCopyService.copymaxnum);
		dlg_copyfile_sb01.setProgress(FileCopyService.copyednum);
		dlg.setView(view);
		dlg.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				myapp.setCopymode(MODE_NONECOPY);
				dlg_copyfile_isshow = false;
				FileCopyService.copyednum = 0;
				FileCopyService.copymaxnum = 0;
				FileCopyService.copyfromfilename.delete(0,
						FileCopyService.copytofilename.length());
				FileCopyService.copytofilename.delete(0,
						FileCopyService.copytofilename.length());
				dialog.cancel();
			}
		});
		dlg.setNegativeButton("隐藏", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dlg_copyfile_isshow = false;
				dialog.cancel();
			}
		});
		dlg_copyfile = dlg.show();
	}

	@Override
	protected void onResume() {
		if (myapp.getCopymode() == MODE_ISCOPY) {
			dlg_copyfile_isshow = true;
			ShowDlgCopyFile();
		}
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		if (screenWidth > screenHeight) {
			scale = screenWidth / screenHeight;

		} else {
			scale = screenHeight / screenWidth;
		}
		SetViewVisible();
		listadapter.notifyDataSetChanged();
		gridadapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcast);
		if (mFtpServer != null) {
			mFtpServer.stop();
			mFtpServer = null;
		}
		if (mFtpConfigfile != null) {
			mFtpConfigfile.delete();
			mFtpConfigfile = null;
		}
		super.onDestroy();
	}

}