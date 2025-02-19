# Quickstart

## Dependencies

This library requires IP2Proxy BIN database to function. You may download the BIN database at

-   IP2Proxy LITE BIN Data (Free): <https://lite.ip2location.com>
-   IP2Proxy Commercial BIN Data (Comprehensive):
    <https://www.ip2location.com>

## Compilation

```bash
javac com/ip2proxy/*.java
jar cf ip2proxy.jar com/ip2proxy/*.class
```

## Sample Codes

### Query geolocation information from BIN database

You can query the geolocation information from the IP2Proxy BIN database as below:

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
			String Threat;
			String Provider;
			String FraudScore;
			
			String IP = "221.121.146.0";
			
			if (Proxy.Open("/usr/data/IP2PROXY-IP-PROXYTYPE-COUNTRY-REGION-CITY-ISP-DOMAIN-USAGETYPE-ASN-LASTSEEN-THREAT-RESIDENTIAL-PROVIDER-FRAUDSCORE.BIN", IP2Proxy.IOModes.IP2PROXY_MEMORY_MAPPED) == 0) {
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
				System.out.println("Threat: " + All.Threat);
				System.out.println("Provider: " + All.Provider);
				System.out.println("Fraud_Score: " + All.Fraud_Score);
				
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
				
				Threat = Proxy.GetThreat(IP);
				System.out.println("Threat: " + Threat);
				
				Provider = Proxy.GetProvider(IP);
				System.out.println("Provider: " + Provider);
				
				FraudScore = Proxy.GetFraudScore(IP);
				System.out.println("FraudScore: " + FraudScore);
			}
			else {
				System.out.println("Error reading BIN file.");
			}
			Proxy.Close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}
```