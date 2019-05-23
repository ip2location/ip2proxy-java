# IP2Proxy Java Component

This component allows user to query an IP address if it was being used as VPN anonymizer, open proxies, web proxies, Tor exits, data center, web hosting (DCH) range and search engine robots (SES). It lookup the proxy IP address from **IP2Proxy BIN Data** file. This data file can be downloaded at

* Free IP2Proxy BIN Data: https://lite.ip2location.com
* Commercial IP2Proxy BIN Data: https://www.ip2location.com/database/ip2proxy

## Compilation

```bash
javac com/ip2proxy/*.java
jar cf ip2proxy.jar com/ip2proxy/*.class
```

## Methods
Below are the methods supported in this class.

|Method Name|Description|
|---|---|
|Open|Open the IP2Proxy BIN data for lookup. Please see the **Usage** section of the 2 modes supported to load the BIN data file.|
|Close|Close and clean up the file pointer.|
|GetPackageVersion|Get the package version (1 to 8 for PX1 to PX8 respectively).|
|GetModuleVersion|Get the module version.|
|GetDatabaseVersion|Get the database version.|
|IsProxy|Check whether if an IP address was a proxy. Returned value:<ul><li>-1 : errors</li><li>0 : not a proxy</li><li>1 : a proxy</li><li>2 : a data center IP address or search engine robot</li></ul>|
|GetAll|Return the proxy information in an object.|
|GetProxyType|Return the proxy type. Please visit <a href="https://www.ip2location.com/databases/px4-ip-proxytype-country-region-city-isp" target="_blank">IP2Location</a> for the list of proxy types supported|
|GetCountryShort|Return the ISO3166-1 country code (2-digits) of the proxy.|
|GetCountryLong|Return the ISO3166-1 country name of the proxy.|
|GetRegion|Return the ISO3166-2 region name of the proxy. Please visit <a href="https://www.ip2location.com/free/iso3166-2" target="_blank">ISO3166-2 Subdivision Code</a> for the information of ISO3166-2 supported|
|GetCity|Return the city name of the proxy.|
|GetISP|Return the ISP name of the proxy.|
|GetDomain|Return the domain name of the proxy.|
|GetUsageType|Return the usage type classification of the proxy. Please visit <a href="https://www.ip2location.com/database/px8-ip-proxytype-country-region-city-isp-domain-usagetype-asn-lastseen" target="_blank">IP2Location</a> for the list of usage types supported.|
|GetASN|Return the autonomous system number of the proxy.|
|GetAS|Return the autonomous system name of the proxy.|
|GetLastSeen|Return the number of days that the proxy was last seen.|

## Usage

Open and read IP2Proxy binary database. There are 2 modes:

1. **IOModes.IP2PROXY_FILE_IO** - File I/O reading. Slower look, but low resource consuming. This is the default.
2. **IOModes.IP2PROXY_MEMORY_MAPPED** - Stores whole IP2Proxy database into a memory-mapped file. Extremely resources consuming. Do not use this mode if your system do not have enough memory.

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
			String Domain;
			String UsageType;
			String ASN;
			String AS;
			String LastSeen;
			
			String IP = "221.121.146.0";
			
			if (Proxy.Open("/usr/data/IP2PROXY-IP-PROXYTYPE-COUNTRY-REGION-CITY-ISP-DOMAIN-USAGETYPE-ASN-LASTSEEN.BIN", IP2Proxy.IOModes.IP2PROXY_MEMORY_MAPPED) == 0) {
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
				System.out.println("Domain: " + All.Domain);
				System.out.println("Usage_Type: " + All.Usage_Type);
				System.out.println("ASN: " + All.ASN);
				System.out.println("AS: " + All.AS);
				System.out.println("Last_Seen: " + All.Last_Seen);
				
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
				
				Domain = Proxy.GetDomain(IP);
				System.out.println("Domain: " + Domain);
				
				UsageType = Proxy.GetUsageType(IP);
				System.out.println("UsageType: " + UsageType);
				
				ASN = Proxy.GetASN(IP);
				System.out.println("ASN: " + ASN);
				
				AS = Proxy.GetAS(IP);
				System.out.println("AS: " + AS);
				
				LastSeen = Proxy.GetLastSeen(IP);
				System.out.println("LastSeen: " + LastSeen);
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

