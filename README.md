IP2Proxy Java Component
=======================

This is the Java component to lookup IP2Proxy databases from https://www.ip2location.com/proxy-database

IP2Proxy Database contains IP addresses which are used as VPN anonymizer, open proxies, web proxies and Tor exits. The database includes records for all public IPv4 addresses.


Compilation
===========
```bash
javac com/ip2proxy/*.java
jar cf ip2proxy.jar com/ip2proxy/*.class
```

Sample Code
===========
```java
import com.ip2proxy.*;

public class Main {
	public Main() {
	}
	
	public static void main(String[] args) {
		try {
			IP2Proxy Proxy = new IP2Proxy();
			ProxyResult All;
			
			int IsProxy;
			String ProxyType;
			String CountryShort;
			String CountryLong;
			String Region;
			String City;
			String ISP;
			
			String IP = "221.121.146.0";
			
			if (Proxy.Open("/usr/data/IP2PROXY-IP-PROXYTYPE-COUNTRY-REGION-CITY-ISP.BIN", IP2Proxy.IOModes.IP2PROXY_MEMORY_MAPPED) == 0) {
				System.out.println("GetModuleVersion: " + Proxy.GetModuleVersion());
				System.out.println("GetPackageVersion: " + Proxy.GetPackageVersion());
				System.out.println("GetDatabaseVersion: " + Proxy.GetDatabaseVersion());
				
				// reading all available fields
				All = Proxy.GetAll(IP);
				System.out.println("Is_Proxy: " + String.valueOf(All.Is_Proxy));
				System.out.println("Proxy_Type: " + All.Proxy_Type);
				System.out.println("Country_Short: " + All.Country_Short);
				System.out.println("Country_Long: " + All.Country_Long);
				System.out.println("Region: " + All.Region);
				System.out.println("City: " + All.City);
				System.out.println("ISP: " + All.ISP);
				
				// reading individual fields
				IsProxy = Proxy.IsProxy(IP);
				System.out.println("Is_Proxy: " + String.valueOf(IsProxy));
				
				ProxyType = Proxy.GetProxyType(IP);
				System.out.println("Proxy_Type: " + ProxyType);
				
				CountryShort = Proxy.GetCountryShort(IP);
				System.out.println("Country_Short: " + CountryShort);
				
				CountryLong = Proxy.GetCountryLong(IP);
				System.out.println("Country_Long: " + CountryLong);
				
				Region = Proxy.GetRegion(IP);
				System.out.println("Region: " + Region);
				
				City = Proxy.GetCity(IP);
				System.out.println("City: " + City);
				
				ISP = Proxy.GetISP(IP);
				System.out.println("ISP: " + ISP);
			}
			else {
				System.out.println("Error reading BIN file.");
			}
			Proxy.Close();
		}
		catch(Exception Ex) {
			System.out.println(Ex);
		}
	}
}
```

