package com.flyzebra.filemanager.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import com.flyzebra.filemanager.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("NewApi")
public class FileCopyService extends Service {
	private MyApp myapp;
	
	//通知栏
	private NotificationManager notimanager = null;
	Notification.Builder  builder = null;
	private final int NOTIFICATION_ID = 1;
	public static int copyednum=0;//已经拷贝的文件数
	public static int copymaxnum=0;//需要拷贝的文件数
	public static StringBuffer copyfromfilename = new StringBuffer();
	public static StringBuffer copytofilename = new StringBuffer();
	
	/*定义复制的文件是在网络上还是在本机，是复制还是剪切*/
	private final int MODE_NONECOPY = 0;	
	private final int MODE_NETCOPY = 1;
	private final int MODE_NETCUT = 2;
	private final int MODE_LOCCOPY = 3;
	private final int MODE_LOCCUT = 4;
	private final int MODE_ISCOPY = 5;
	
	private NtlmPasswordAuthentication auth=null;
	
	@Override
	public void onCreate() {		
		super.onCreate();	
		Log.i("FileManager","onCreate");
		myapp=(MyApp) getApplication();
		if(notimanager==null){
			notimanager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		if(builder==null){
			builder = new Notification.Builder(this);        
		}
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent PdIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setTicker("复制文件...");
		builder.setContentIntent(PdIntent);	
		builder.setSmallIcon(R.drawable.but_copy);
		builder.setOngoing(true);
		
		auth = new NtlmPasswordAuthentication("", myapp.getUsername(), myapp.getPassword());
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("FileManager","onStartCommand");
		CopyFile();	
		return super.onStartCommand(intent, flags, startId);
	}
	
	private int CountLocalFile(File src)
	{
		int count=0;
		if (src.isFile()) {
			count=1;
		}
		if (src.isDirectory())
		{
			File f[]=src.listFiles();
			for(int i=0;i<f.length;i++){
				count=count+CountLocalFile(f[i]);
			}			
		}
		return count;
	}
	

	private int CountNetWorkFile(SmbFile src)
	{
		int count = 0;
		if (myapp.getCopymode() == MODE_ISCOPY) {			
			String filename = src.getPath();
			if (filename.lastIndexOf("/") == filename.length() - 1) {
				try {
					SmbFile[] f = src.listFiles();
					for (int i = 0; i < f.length; i++) {
						count = count + CountNetWorkFile(f[i]);
					}
				} catch (SmbException e) {
					e.printStackTrace();
				}
			} else {
				count = 1;
			}			
		}
		return count;
	}
	
	private void CopyFile(){		
		switch (myapp.getCopymode()){
		//本机内容复制
		case MODE_LOCCOPY:	
			myapp.setCopymode(MODE_ISCOPY);
			StartLOCCOPY();			
			break;
		//本机内容剪切
		case MODE_LOCCUT:
			myapp.setCopymode(MODE_ISCOPY);
			StartLOCCUT();	
			break;
		//网络复制
		case MODE_NETCOPY:	
			myapp.setCopymode(MODE_ISCOPY);
			StartNETCOPY();
			break;
		//网络剪切
		case MODE_NETCUT:
			myapp.setCopymode(MODE_ISCOPY);
			//网络剪切尚未开发			
			break;
		}
	}

	private void StartNETCOPY() {
		Thread netcopy = new Thread(new Runnable(){
			@Override
			public void run() {
				copyednum=0;
				copymaxnum=0;	
				builder.setContentTitle("正在统计复制文件的数量...");					
				notimanager.notify(NOTIFICATION_ID, builder.build());
//				System.setProperty("jcifs.smb.client.dfs.disabled", "true");
				for(String path:myapp.getCopylist()){
					try {
						copymaxnum=copymaxnum+CountNetWorkFile(new SmbFile(path,auth));
						myapp.SendMainBroadcast("BROADCAST_MAIN_UPDLGCOPY_MAX");
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}						
				for(String spath:myapp.getCopylist()){
					SmbFile sf = null;
					try {
						sf = new SmbFile(spath,auth);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					String name=null;
					if(spath.lastIndexOf("/")==spath.length()-1){
						name = spath.substring(0,spath.length()-1);
						name = name.substring(name.lastIndexOf("/")+1,name.length());
					}
					else{
						name = spath.substring(spath.lastIndexOf("/")+1,spath.length());
					}
					File of = new File(myapp.getCopytopath()+File.separator+name);
					CopyNetWorkFile(sf ,of);
					myapp.SendMainBroadcast("BROADCAST_MAIN_UPVIEW");
				}
				copyednum=0;
				copymaxnum=0;
				copytofilename.delete(0, copytofilename.length());
				copytofilename.delete(0, copyfromfilename.length());
				myapp.getCopylist().clear();
				notimanager.cancelAll();
				myapp.setCopymode(MODE_NONECOPY);
				myapp.SendMainBroadcast("BROADCAST_MAIN_UPDLGCOPY_END");
				stopSelf();					
			}});	
		netcopy.setPriority(Thread.MAX_PRIORITY);
		netcopy.start();
	}

	private void StartLOCCUT() {
		Log.i("TESTLOG",myapp.getCopylist().toString());
		new Thread(new Runnable(){
			@Override
			public void run() {
				copyednum=0;
				copymaxnum=0;	
				copymaxnum=myapp.getCopylist().size();	
				myapp.SendMainBroadcast("BROADCAST_MAIN_UPDLGCOPY_MAX");
				for(String spath:myapp.getCopylist()){
					File sf = new File(spath);
					File of = new File(myapp.getCopytopath()+File.separator+sf.getName());
					MoveLocalFile(sf ,of);						
				}
				myapp.getCopylist().clear();
				notimanager.cancelAll();
				myapp.setCopymode(MODE_NONECOPY);
				myapp.SendMainBroadcast("BROADCAST_MAIN_UPVIEW");
				myapp.SendMainBroadcast("BROADCAST_MAIN_UPDLGCOPY_END");
				stopSelf();					
			}}).start();
	}

	private void StartLOCCOPY() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				copyednum=0;
				copymaxnum=0;
				builder.setContentTitle("正在统计复制文件的数量...");					
				notimanager.notify(NOTIFICATION_ID, builder.build());
				for(String path:myapp.getCopylist()){
					copymaxnum=copymaxnum+CountLocalFile(new File(path));
				}
				myapp.SendMainBroadcast("BROADCAST_MAIN_UPDLGCOPY_MAX");
//				System.setProperty("jcifs.smb.client.dfs.disabled", "true");
				for(String spath:myapp.getCopylist()){						
					File sf = new File(spath);
					File of = new File(myapp.getCopytopath()+File.separator+sf.getName());
					CopyLocalFile(sf ,of);
					myapp.SendMainBroadcast("BROADCAST_MAIN_UPVIEW");
				}
				myapp.getCopylist().clear();
				notimanager.cancelAll();
				myapp.setCopymode(MODE_NONECOPY);
				myapp.SendMainBroadcast("BROADCAST_MAIN_UPDLGCOPY_END");
				stopSelf();					
			}}).start();		
	}

	private void MoveLocalFile(File sf, File of) {
		builder.setContentTitle("正在移动文件(" + copyednum + "/" + copymaxnum + ")");
		builder.setContentText(sf.getName());
		builder.setProgress(copymaxnum, copyednum, false);
		notimanager.notify(NOTIFICATION_ID, builder.build());
		copyfromfilename.delete(0, copytofilename.length());
		copyfromfilename.append(sf.getPath());
		copytofilename.delete(0, copytofilename.length());
		copytofilename.append(of.getAbsolutePath());		
		sf.renameTo(of);
		copyednum++;	
		myapp.SendMainBroadcast("BROADCAST_MAIN_UPDLGCOPY_PRO");
	}

	private void CopyLocalFile(File sf, File of) {
		
	}
	
	private void CopyNetWorkFile(SmbFile sf, File of) {
		if (myapp.getCopymode() == MODE_ISCOPY) {
			String filename = sf.getPath();
			if (filename.lastIndexOf("/") == filename.length() - 1) {
				of.mkdir();
				try {
					SmbFile[] fl = sf.listFiles();
					for (int i = 0; i < fl.length; i++) {
						String name = null;
						if (fl[i].getName().lastIndexOf("/") == fl[i].getName().length() - 1) {
							name = fl[i].getName().substring(0,	fl[i].getName().length() - 1);
						} else {
							name = fl[i].getName();
						}
						CopyNetWorkFile(fl[i], new File(of.getAbsolutePath() +"/" + name));
					}
				} catch (SmbException e) {
					e.printStackTrace();
				}
			} else {
				builder.setContentTitle("正在复制文件(" + copyednum + "/" + copymaxnum + ")");
				copyfromfilename.delete(0, copytofilename.length());
				copyfromfilename.append(sf.getPath());
				copytofilename.delete(0, copytofilename.length());
				copytofilename.append(of.getAbsolutePath());
				builder.setContentText(copytofilename);
				builder.setProgress(copymaxnum, copyednum, false);
				notimanager.notify(NOTIFICATION_ID, builder.build());
				myapp.SendMainBroadcast("BROADCAST_MAIN_UPDLGCOPY_PRO");
				try {
					InputStream is = new SmbFileInputStream(sf);
					OutputStream op = new FileOutputStream(of);
					BufferedInputStream bis = new BufferedInputStream(is);
					BufferedOutputStream bos = new BufferedOutputStream(op);
					byte[] bt = new byte[8192];
					int len = bis.read(bt);
					while (len != -1 && (myapp.getCopymode() == MODE_ISCOPY)) {
						bos.write(bt, 0, len);
						len = bis.read(bt);
					}
					bis.close();
					bos.close();
					copyednum++;
				} catch (SmbException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
			
	}
//	private void FileDelete(File f){
//		if(f.isDirectory()){
//			if (null != f.listFiles()) {
//				for (File f1 : f.listFiles()) {
//					FileDelete(f1);
//				}
//			}
//			f.delete();
//		}
//		else if(f.isFile()){
//			f.delete();
//		}
//	}
}
