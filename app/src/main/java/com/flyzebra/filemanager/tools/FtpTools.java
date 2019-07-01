package com.flyzebra.filemanager.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

public class FtpTools {
	
	public static void ConfigFile(String path,File file,String charset){
//		try {
//			path =  URLEncoder.encode(path,charset);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		String config = "ftpserver.user.admin.userpassword=21232F297A57A5A743894A0E4A801FC3\r\n"+
				"ftpserver.user.admin.homedirectory="+path+"\r\n"+
				"ftpserver.user.admin.enableflag=true\r\n"+
				"ftpserver.user.admin.writepermission=true\r\n"+
				"ftpserver.user.admin.maxloginnumber=0\r\n"+
				"ftpserver.user.admin.maxloginperip=0\r\n"+
				"ftpserver.user.admin.idletime=0\r\n"+
				"ftpserver.user.admin.uploadrate=0\r\n"+
				"ftpserver.user.admin.downloadrate=0\r\n\r\n"+				
				"ftpserver.user.anonymous.userpassword=\r\n"+
				"ftpserver.user.anonymous.homedirectory="+path+"\r\n"+
				"ftpserver.user.anonymous.enableflag=true\r\n"+
				"ftpserver.user.anonymous.writepermission=true\r\n"+
				"ftpserver.user.anonymous.maxloginnumber=0\r\n"+
				"ftpserver.user.anonymous.maxloginperip=0\r\n"+
				"ftpserver.user.anonymous.idletime=0\r\n"+
				"ftpserver.user.anonymous.uploadrate=0\r\n"+
				"ftpserver.user.anonymous.downloadrate=0\r\n";		
			try {
				OutputStream out = new FileOutputStream(file);
				out.write(config.getBytes(charset), 0, config.getBytes(charset).length);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
	}
	
	public static FtpServer startFtpServer(String path ,File file, int port,String charset) {
		System.setProperty("java.net.preferIPv6Addresses", "false");
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		ConfigFile(path, file,charset);
		userManagerFactory.setFile(file);
		UserManager user = userManagerFactory.createUserManager();		
		serverFactory.setUserManager(user);
		factory.setPort(port);
		serverFactory.addListener("default", factory.createListener());
		FtpServer server = serverFactory.createServer();
		return server;
	}

}
