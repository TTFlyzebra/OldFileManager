package com.flyzebra.filemanager.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.flyzebra.filemanager.tools.MobilePC;

import android.app.Application;
import android.content.Intent;

public class MyApp extends Application {	
	public static final String TAG = "com.flyzebra";
	private MobilePC localMobilePC = null;
	private String copytopath = null;
	private int copymode = 0;
	private String show="HOME";
	private List<HashMap<String, Object>> list = null;
	private List<String> copylist = null;
	private List<String> dellist =null;
	private Stack<String> pathstack = null;	
	private int showmenu = 0;
	private int copyfrom = 0;
	private int showwhat = 1;
	private int showview = 3;
	private List<Map<String,Object>> upIamge_list = null;
	private Map<String,Object> upImage_map = null;
	public static boolean upImage_map_lock = false;
	
	public Map<String, Object> getUpImage_map() {
		if(upImage_map==null){
			upImage_map = new HashMap<String,Object>();
		}
		return upImage_map;
	}

	public void setUpImage_map(Map<String, Object> upImage_map) {
		this.upImage_map = upImage_map;
	}

	private String username = "GUEST";;
	public List<Map<String, Object>> getUpIamge_list() {
		if(upIamge_list==null){
			upIamge_list = new ArrayList<Map<String,Object>>();
		}
		return upIamge_list;
	}

	public void setUpIamge_list(List<Map<String, Object>> upIamge_list) {
		this.upIamge_list = upIamge_list;
	}

	private String password = "";
	
	@Override
	public void onCreate() {
		super.onCreate();
	}	
	
	public void setShowview(int showview) {
		this.showview = showview;
	}

	public String getCopytopath() {
		return copytopath;
	}

	public void setCopytopath(String copytopath) {
		this.copytopath = copytopath;
	}

	public int getShowmenu() {
		return showmenu;
	}

	public void setShowmenu(int showmenu) {
		this.showmenu = showmenu;
	}

	public int getCopyfrom() {
		return copyfrom;
	}

	public void setCopyfrom(int copyfrom) {
		this.copyfrom = copyfrom;
	}

	public int getCopymode() {
		return copymode;
	}

	public void setCopymode(int copymode) {
		this.copymode = copymode;
	}

	public String getUsername() {
		return username;
	}

	public int getShowwhat() {
		return showwhat;
	}

	public void setShowwhat(int showwhat) {
		this.showwhat = showwhat;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Stack<String> getPathstack() {
		if (pathstack == null) {
			pathstack = new Stack<String>();
		}
		return pathstack;
	}

	public void setPathstack(Stack<String> pathstack) {
		this.pathstack = pathstack;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public int getShowview() {
		return showview;
	}

	public List<HashMap<String, Object>> getList() {
		if (list == null) {
			list = new ArrayList<HashMap<String, Object>>();
		}
		return list;
	}

	public void setList(List<HashMap<String, Object>> list) {
		this.list = list;
	}

	public MyApp() {
	}

	public MobilePC getLocalMobilePC() {
		if (localMobilePC == null) {
			localMobilePC = new MobilePC();
		}
		return localMobilePC;
	}

	public void setLocalMobilePC(MobilePC localMobilePC) {
		this.localMobilePC = localMobilePC;
	}

	public List<String> getCopylist() {
		if(copylist==null){
			copylist=new ArrayList<String>();
		}
		return copylist;
	}

	public void setCopylist(List<String> copylist) {
		this.copylist = copylist;
	}
	public List<String> getDellist() {
		if(dellist==null){
			dellist = new ArrayList<String>();
		}
		return dellist;
	}

	public void setDellist(List<String> dellist) {
		this.dellist = dellist;
	}
	
	public void SendMainBroadcast(String action){
		Intent intent = new Intent();
		intent.setAction(action);
		sendBroadcast(intent);
	}

}
