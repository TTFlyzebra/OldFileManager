package com.flyzebra.filemanager.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.apache.http.conn.util.InetAddressUtils;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MobilePC {
	public static String getHostIP() {
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有IP
				Enumeration<InetAddress> inet = nif.getInetAddresses();
				// 遍历每一个接口绑定的所有IP
				while (inet.hasMoreElements()) {
					InetAddress inetaddress = inet.nextElement();
					if (!inetaddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetaddress.getHostAddress())) {
						return inetaddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ipaddress;

	}

	// 得到本机Mac地址
	public static String getMac(WifiManager wifimanager) {
		// WifiManager wifimanager = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfor = wifimanager.getConnectionInfo();
		return wifiInfor.getMacAddress();
	}
}
