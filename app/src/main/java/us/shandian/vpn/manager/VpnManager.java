package us.shandian.vpn.manager;

import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.group.mm.wechat.main.MainActivity;
import com.group.mm.wechat.main.ShellUtils;
import com.group.mm.wechat.motionClick.Utils;

import java.io.IOException;
import java.lang.StringBuilder;

import us.shandian.vpn.util.RunCommand;

public class VpnManager
{
	private static final String PPP_UNIT = "0";
	private static final String PPP_INTERFACE = "ppp" + PPP_UNIT;
	private static final int MAX_WAIT_TIME = 30; // seconds
	
	// Start connection to a PPTP server
	public static boolean startVpn(VpnProfile p) {
		// Check
		if (TextUtils.isEmpty(p.server) || TextUtils.isEmpty(p.username) ||
			TextUtils.isEmpty(p.password)) {
			
			return false;
		}
		
		// Iface
		String iface = getDefaultIface();
		Log.d("mtpd","iface"+iface);
		
		// Arguments to mtpd
		String[] args = new String[]{"wlan0", "pptp", p.server, "1723", "linkname", "vpn","persist","usepeerdns","defaultroute","noauth","noipdefault", "mtu", "1400", "mru", "1400", (p.mppe ? "+mppe" : "nomppe"),
				"unit", PPP_UNIT,"nodetach", "name", p.username,
				"password", p.password,"ipparam","vpn"};
		
		// Start
		startMtpd(args);
		
		// Wait for mtpd
		if (!blockUntilStarted()) {
			return false;
		}
		
		// Set up ip route
		setupRoute();
		
		// Set up dns
		setupDns(p);
		
		return true;
	}
	
	public static void stopVpn() {
		// Kill all vpn stuff
		StringBuilder s = new StringBuilder();
		s.append("pkill mtpd\n")
		 .append("pkill pppd\n")
		 .append("ip ro flush dev ").append(PPP_INTERFACE).append("\n")
		 .append("iptables -t nat -F\n")
		 .append("iptables -t nat -X\n")
		 .append("iptables -t nat -Z");
		
		try {
			RunCommand.run(s.toString()).waitFor();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isVpnRunning() {
		try {
           ShellUtils.CommandResult sdfs = ShellUtils.execCommand("ip ro\n",false);
           if(sdfs!=null&&sdfs.successMsg!=null){
               String sdf = sdfs.successMsg.replace("\n", "").trim();
               Log.d("LLLL--",sdf);
               if (!TextUtils.isEmpty(sdf)&&(sdf.contains("ppp0")||sdf.contains("ppp100"))) {
                   Log.d("LLLL","ppp0 yes");
                   return true;
               }
           }
		} catch (Exception e) {
			Log.d("LLLL","e："+e.getLocalizedMessage());
		}
		Log.d("LLLL","ppp0 NO");
		return false;
	}

	public static void open_fly(){
		//
		Log.d("LLL","open_fly");
		try {
			RunCommand.run("settings put global airplane_mode_on 1");
			try {
				Thread.sleep(1000*1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			RunCommand.run("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
			try {
				Thread.sleep(1000*5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}



	}



	public static void click_vpn(){
        ShellUtils.execCommand("input tap 680 280",true,false);
	}

	public static void click_vpn(boolean isFlage){
		if(!isFlage){
			ShellUtils.execCommand("input tap 680 280",true,false);
		}else{
			ShellUtils.execCommand("input tap 900 360",true,false);
		}
	}

	public static void close_fly(){
		//
		Log.d("LLL","close_fly");
		try {
			RunCommand.run("settings put global airplane_mode_on 0");
			try {
				Thread.sleep(1000*1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			RunCommand.run("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
			try {
				Thread.sleep(1000*3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// settings get system vpn_enable_key
	public static String get_vpn_enable(){
		String routes;

		try {
			Process p = RunCommand.run("settings get system vpn_enable_key");
			p.waitFor();
			routes = RunCommand.readInput(p);
			Log.d("LLLL","get_vpn_enable:"+routes);
		} catch (Exception e) {
			routes = null;
		}
		Log.d("LLL","get_vpn_enable---:"+routes);
		return routes;
	}
	
	private static String getDefaultIface() {
		String routes;
		
		try {
			Process p = RunCommand.run("ip ro");
			p.waitFor();
			routes = RunCommand.readInput(p);
		} catch (Exception e) {
			routes = null;
		}
		
		if (routes != null) {
			for (String route : routes.split("\n")) {
				if (route.startsWith("default")) {
					String iface = null;
					boolean last = false;
					for (String ele : route.split(" ")) {
						if (last) {
							iface = ele;
							break;
						} else if (ele.equals("dev")) {
							last = true;
						}
					}
					
					if (iface != null) {
						return iface;
					} else {
						break;
					}
				}
			}
		}
		
		// Can't load default interface? That's not possible.
		return "eth0";
	}
	
	private static void startMtpd(String[] args) {
		StringBuilder s = new StringBuilder();
		s.append("mtpd");
		get_vpn_enable();
		// Add args
		for (String arg : args) {
			s.append(" ").append(arg);
		}
		Log.d("LLLL",s.toString());
		// Run
		try {
			RunCommand.run(s.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static boolean blockUntilStarted() {
		int n = MAX_WAIT_TIME * 2;
		
		for (int i = 0; i < n; i++) {
			try {
				Process p = RunCommand.run("ip ro");
				p.waitFor();
				String out = RunCommand.readInput(p);
				
				if (out.contains(PPP_INTERFACE)) {
					return true;
				} else {
					Thread.sleep(500);
				}
			} catch (Exception e) {
				break;
			}
		}
		
		return false;
	}
	
	private static void setupRoute() {
		StringBuilder s = new StringBuilder();
		s.append("ip ru add from all table 200 \n")
		 .append("ip ro add default dev ").append(PPP_INTERFACE).append(" table 200");
		
		// Run
		try {
			Process p = RunCommand.run(s.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void setupDns(VpnProfile profile) {
		// For now, I haven't got any idea of how to get the DNS returned by pppd
		// So we just use 8.8.8.8 and 8.8.4.4
		
		String dns1 = null, dns2 = null;
		
		try {
			Process p = RunCommand.run("getprop net.dns1");
			p.waitFor();
			dns1 = RunCommand.readInput(p).replace("\n", "").trim();
			p = RunCommand.run("getprop net.dns2");
			p.waitFor();
			dns2 = RunCommand.readInput(p).replace("\n", "").trim();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (TextUtils.isEmpty(dns1) || TextUtils.isEmpty(dns2)) {
			return;
		}
		
		StringBuilder s = new StringBuilder();
		s.append("iptables -t nat -A OUTPUT -d ").append(dns1).append("/32 -o ")
			.append(PPP_INTERFACE).append(" -p udp -m udp --dport 53 -j DNAT --to-destination ").append(profile.dns1).append(":53\n")
		 .append("iptables -t nat -A OUTPUT -d ").append(dns2).append("/32 -o ")
			.append(PPP_INTERFACE).append(" -p udp -m udp --dport 53 -j DNAT --to-destination ").append(profile.dns2).append(":53");
		
		try {
			RunCommand.run(s.toString()).waitFor();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
}
