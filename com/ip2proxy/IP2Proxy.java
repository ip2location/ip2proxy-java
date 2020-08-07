package com.ip2proxy;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.lang.StringBuilder;

public class IP2Proxy {
	private static final Pattern Pattern1 = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"); // IPv4
	private static final Pattern Pattern2 = Pattern.compile("^([0-9A-F]{1,4}:){6}(0[0-9]+\\.|.*?\\.0[0-9]+).*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern Pattern3 = Pattern.compile("^[0-9]+$");
	private static final Pattern Pattern4 = Pattern.compile("^(.*:)(([0-9]+\\.){3}[0-9]+)$");
	private static final Pattern Pattern5 = Pattern.compile("^.*((:[0-9A-F]{1,4}){2})$");
	private static final Pattern Pattern6 = Pattern.compile("^[0:]+((:[0-9A-F]{1,4}){1,2})$", Pattern.CASE_INSENSITIVE);
	private static final Pattern Pattern7 = Pattern.compile("^([0-9]+\\.){1,2}[0-9]+$");
	private static final BigInteger MAX_IPV4_RANGE = new BigInteger("4294967295");
	private static final BigInteger MAX_IPV6_RANGE = new BigInteger("340282366920938463463374607431768211455");
	private static final BigInteger FROM_6TO4 = new BigInteger("42545680458834377588178886921629466624");
	private static final BigInteger TO_6TO4 = new BigInteger("42550872755692912415807417417958686719");
	private static final BigInteger FROM_TEREDO = new BigInteger("42540488161975842760550356425300246528");
	private static final BigInteger TO_TEREDO = new BigInteger("42540488241204005274814694018844196863");
	private static final BigInteger LAST_32BITS = new BigInteger("4294967295");
	
	private static final String MSG_NOT_SUPPORTED = "NOT SUPPORTED";
	private static final String MSG_INVALID_IP = "INVALID IP ADDRESS";
	private static final String MSG_MISSING_FILE = "MISSING FILE";
	private static final String MSG_IPV6_UNSUPPORTED = "IPV6 ADDRESS MISSING IN IPV4 BIN";
	
	public enum IOModes {
		IP2PROXY_FILE_IO,
		IP2PROXY_MEMORY_MAPPED;
	}
	
	private enum Modes {
		COUNTRY_SHORT,
		COUNTRY_LONG,
		REGION,
		CITY,
		ISP,
		PROXY_TYPE,
		IS_PROXY,
		DOMAIN,
		USAGE_TYPE,
		ASN,
		AS,
		LAST_SEEN,
		THREAT,
		ALL;
	}
	
	private static final int COUNTRY_POSITION[] = {0, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3};
	private static final int REGION_POSITION[] = {0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4};
	private static final int CITY_POSITION[] = {0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5};
	private static final int ISP_POSITION[] = {0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6};
	private static final int PROXYTYPE_POSITION[] = {0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2};
	private static final int DOMAIN_POSITION[] = {0, 0, 0, 0, 0, 7, 7, 7, 7, 7, 7};
	private static final int USAGETYPE_POSITION[] = {0, 0, 0, 0, 0, 0, 8, 8, 8, 8, 8};
	private static final int ASN_POSITION[] = {0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9};
	private static final int AS_POSITION[] = {0, 0, 0, 0, 0, 0, 0, 10, 10, 10, 10};
	private static final int LASTSEEN_POSITION[] = {0, 0, 0, 0, 0, 0, 0, 0, 11, 11, 11};
	private static final int THREAT_POSITION[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 12};
	
	private MappedByteBuffer _IPv4Buffer = null;
	private MappedByteBuffer _IPv6Buffer = null;
	private MappedByteBuffer _MapDataBuffer = null;
	private int[][] _IndexArrayIPv4 = new int[65536][2];
	private int[][] _IndexArrayIPv6 = new int[65536][2];
	private long _IPv4Offset = 0;
	private long _IPv6Offset = 0;
	private long _MapDataOffset = 0;
	private int _IPv4ColumnSize = 0;
	private int _IPv6ColumnSize = 0;
	
	private int _BaseAddr = 0;
	private int _DBCount = 0;
	private int _DBColumn = 0;
	private int _DBType = 0;
	private int _DBDay = 1;
	private int _DBMonth = 1;
	private int _DBYear = 1;
	private int _BaseAddrIPv6 = 0;
	private int _DBCountIPv6 = 0;
	private int _IndexBaseAddr = 0;
	private int _IndexBaseAddrIPv6 = 0;
	
	private boolean _UseMemoryMappedFile = false;
	private String _IPDatabasePath = "";
	
	private int COUNTRY_POSITION_OFFSET;
	private int REGION_POSITION_OFFSET;
	private int CITY_POSITION_OFFSET;
	private int ISP_POSITION_OFFSET;
	private int PROXYTYPE_POSITION_OFFSET;
	private int DOMAIN_POSITION_OFFSET;
	private int USAGETYPE_POSITION_OFFSET;
	private int ASN_POSITION_OFFSET;
	private int AS_POSITION_OFFSET;
	private int LASTSEEN_POSITION_OFFSET;
	private int THREAT_POSITION_OFFSET;
	
	private boolean COUNTRY_ENABLED;
	private boolean REGION_ENABLED;
	private boolean CITY_ENABLED;
	private boolean ISP_ENABLED;
	private boolean PROXYTYPE_ENABLED;
	private boolean DOMAIN_ENABLED;
	private boolean USAGETYPE_ENABLED;
	private boolean ASN_ENABLED;
	private boolean AS_ENABLED;
	private boolean LASTSEEN_ENABLED;
	private boolean THREAT_ENABLED;
	
	private static final String _ModuleVersion = "3.0.0";
	
	public IP2Proxy() {
	
	}
	
/**
* This function returns the module version.
* @return Module version
*/
	public String GetModuleVersion() {
		return _ModuleVersion;
	}
	
/**
* This function returns the package version.
* @return Package version
*/
	public String GetPackageVersion() {
		return String.valueOf(_DBType);
	}
	
/**
* This function returns the IP database version.
* @return IP database version
*/
	public String GetDatabaseVersion() {
		if (_DBYear == 0) {
			return "";
		}
		else {
			return "20" + String.valueOf(_DBYear) + "." + String.valueOf(_DBMonth) + "." + String.valueOf(_DBDay);
		}
	}
	
/**
* This function returns ans integer to state if it proxy.
* @param IP IP Address you wish to query
* @return -1 if error, 0 if not a proxy, 1 if proxy except DCH and SES, 2 if proxy and either DCH or SES
*/
	public int IsProxy(String IP) throws IOException {
		return ProxyQuery(IP, Modes.IS_PROXY).Is_Proxy;
	}
	
/**
* This function returns the country code.
* @param IP IP Address you wish to query
* @return Country code
*/
	public String GetCountryShort(String IP) throws IOException {
		return ProxyQuery(IP, Modes.COUNTRY_SHORT).Country_Short;
	}
	
/**
* This function returns the country name.
* @param IP IP Address you wish to query
* @return Country name
*/
	public String GetCountryLong(String IP) throws IOException {
		return ProxyQuery(IP, Modes.COUNTRY_LONG).Country_Long;
	}
	
/**
* This function returns the region name.
* @param IP IP Address you wish to query
* @return Region name
*/
	public String GetRegion(String IP) throws IOException {
		return ProxyQuery(IP, Modes.REGION).Region;
	}
	
/**
* This function returns the city name.
* @param IP IP Address you wish to query
* @return City name
*/
	public String GetCity(String IP) throws IOException {
		return ProxyQuery(IP, Modes.CITY).City;
	}
	
/**
* This function returns the ISP name.
* @param IP IP Address you wish to query
* @return ISP name
*/
	public String GetISP(String IP) throws IOException {
		return ProxyQuery(IP, Modes.ISP).ISP;
	}
	
/**
* This function returns the proxy type.
* @param IP IP Address you wish to query
* @return Proxy type
*/
	public String GetProxyType(String IP) throws IOException {
		return ProxyQuery(IP, Modes.PROXY_TYPE).Proxy_Type;
	}
	
/**
* This function returns the domain.
* @param IP IP Address you wish to query
* @return Domain
*/
	public String GetDomain(String IP) throws IOException {
		return ProxyQuery(IP, Modes.DOMAIN).Domain;
	}
	
/**
* This function returns the usage type.
* @param IP IP Address you wish to query
* @return Proxy type
*/
	public String GetUsageType(String IP) throws IOException {
		return ProxyQuery(IP, Modes.USAGE_TYPE).Usage_Type;
	}
	
/**
* This function returns the Autonomous System Number.
* @param IP IP Address you wish to query
* @return Autonomous System Number
*/
	public String GetASN(String IP) throws IOException {
		return ProxyQuery(IP, Modes.ASN).ASN;
	}
	
/**
* This function returns the Autonomous System name.
* @param IP IP Address you wish to query
* @return Autonomous System name
*/
	public String GetAS(String IP) throws IOException {
		return ProxyQuery(IP, Modes.AS).AS;
	}
	
/**
* This function returns number of days the proxy was last seen.
* @param IP IP Address you wish to query
* @return Number of days last seen
*/
	public String GetLastSeen(String IP) throws IOException {
		return ProxyQuery(IP, Modes.LAST_SEEN).Last_Seen;
	}
	
/**
* This function returns the threat type of the proxy.
* @param IP IP Address you wish to query
* @return Threat type of the proxy
*/
	public String GetThreat(String IP) throws IOException {
		return ProxyQuery(IP, Modes.THREAT).Threat;
	}
	
/**
* This function returns proxy result.
* @param IP IP Address you wish to query
* @return Proxy result
*/
	public ProxyResult GetAll(String IP) throws IOException {
		return ProxyQuery(IP);
	}
	
/**
* This function destroys the mapped bytes.
*/
	public int Close() {
		DestroyMappedBytes();
		_BaseAddr = 0;
		_DBCount = 0;
		_DBColumn = 0;
		_DBType = 0;
		_DBDay = 1;
		_DBMonth = 1;
		_DBYear = 1;
		_BaseAddrIPv6 = 0;
		_DBCountIPv6 = 0;
		_IndexBaseAddr = 0;
		_IndexBaseAddrIPv6 = 0;
		return 0;
	}
	
	private void DestroyMappedBytes() {
		if (_IPv4Buffer != null) {
			_IPv4Buffer = null;
		}
		if (_IPv6Buffer != null) {
			_IPv6Buffer = null;
		}
		if (_MapDataBuffer != null) {
			_MapDataBuffer = null;
		}
	}
	
	private void CreateMappedBytes() throws IOException {
		RandomAccessFile RF = null;
		
		try {
			RF = new RandomAccessFile(_IPDatabasePath, "r");
			final FileChannel InChannel = RF.getChannel();
			CreateMappedBytes(InChannel);
		}
		catch (IOException Ex) {
			throw Ex;
		}
		finally {
			if (RF != null) {
				RF.close();
				RF = null;
			}
		}
	}
	
	private void CreateMappedBytes(FileChannel InChannel) throws IOException {
		try {
			if (_IPv4Buffer == null) {
				final long _IPv4Bytes = (long)_IPv4ColumnSize * (long)_DBCount;
				_IPv4Offset = _BaseAddr - 1;
				_IPv4Buffer = InChannel.map(FileChannel.MapMode.READ_ONLY, _IPv4Offset, _IPv4Bytes);
				_IPv4Buffer.order(ByteOrder.LITTLE_ENDIAN);
				_MapDataOffset = _IPv4Offset + _IPv4Bytes;
			}
			
			if (_DBCountIPv6 > 0 && _IPv6Buffer == null) {
				final long _IPv6Bytes = (long)_IPv6ColumnSize * (long)_DBCountIPv6;
				_IPv6Offset = _BaseAddrIPv6 - 1;
				_IPv6Buffer = InChannel.map(FileChannel.MapMode.READ_ONLY, _IPv6Offset, _IPv6Bytes);
				_IPv6Buffer.order(ByteOrder.LITTLE_ENDIAN);
				_MapDataOffset = _IPv6Offset + _IPv6Bytes;
			}
			
			if (_MapDataBuffer == null) {
				_MapDataBuffer = InChannel.map(FileChannel.MapMode.READ_ONLY, _MapDataOffset, InChannel.size() - _MapDataOffset);
				_MapDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
			}
		}
		catch (IOException Ex) {
			throw Ex;
		}
	}
	
	private boolean LoadBIN() throws IOException {
		boolean LoadOK = false;
		RandomAccessFile RF = null;
		
		try {
			if (_IPDatabasePath.length() > 0) {
				RF = new RandomAccessFile(_IPDatabasePath, "r");
				final FileChannel InChannel = RF.getChannel();
				final MappedByteBuffer _HeaderBuffer = InChannel.map(FileChannel.MapMode.READ_ONLY, 0, 64); // 64 bytes header
				
				_HeaderBuffer.order(ByteOrder.LITTLE_ENDIAN);
				
				_DBType = _HeaderBuffer.get(0);
				_DBColumn = _HeaderBuffer.get(1);
				_DBYear = _HeaderBuffer.get(2);
				_DBMonth = _HeaderBuffer.get(3);
				_DBDay = _HeaderBuffer.get(4);
				_DBCount = _HeaderBuffer.getInt(5); // 4 bytes
				_BaseAddr = _HeaderBuffer.getInt(9); // 4 bytes
				_DBCountIPv6 = _HeaderBuffer.getInt(13); // 4 bytes
				_BaseAddrIPv6 = _HeaderBuffer.getInt(17); // 4 bytes
				_IndexBaseAddr = _HeaderBuffer.getInt(21); //4 bytes
				_IndexBaseAddrIPv6 = _HeaderBuffer.getInt(25); //4 bytes
				
				_IPv4ColumnSize = _DBColumn << 2; // 4 bytes each column
				_IPv6ColumnSize = 16 + ((_DBColumn - 1) << 2); // 4 bytes each column, except IPFrom column which is 16 bytes
				
				// since both IPv4 and IPv6 use 4 bytes for the below columns, can just do it once here
				// COUNTRY_POSITION_OFFSET = (COUNTRY_POSITION[_DBType] != 0) ? (COUNTRY_POSITION[_DBType] - 1) << 2 : 0;
				// REGION_POSITION_OFFSET = (REGION_POSITION[_DBType] != 0) ? (REGION_POSITION[_DBType] - 1) << 2 : 0;
				// CITY_POSITION_OFFSET = (CITY_POSITION[_DBType] != 0) ? (CITY_POSITION[_DBType] - 1) << 2 : 0;
				// ISP_POSITION_OFFSET = (ISP_POSITION[_DBType] != 0) ? (ISP_POSITION[_DBType] - 1) << 2 : 0;
				// PROXYTYPE_POSITION_OFFSET = (PROXYTYPE_POSITION[_DBType] != 0) ? (PROXYTYPE_POSITION[_DBType] - 1) << 2 : 0;
				// DOMAIN_POSITION_OFFSET = (DOMAIN_POSITION[_DBType] != 0) ? (DOMAIN_POSITION[_DBType] - 1) << 2 : 0;
				// USAGETYPE_POSITION_OFFSET = (USAGETYPE_POSITION[_DBType] != 0) ? (USAGETYPE_POSITION[_DBType] - 1) << 2 : 0;
				// ASN_POSITION_OFFSET = (ASN_POSITION[_DBType] != 0) ? (ASN_POSITION[_DBType] - 1) << 2 : 0;
				// AS_POSITION_OFFSET = (AS_POSITION[_DBType] != 0) ? (AS_POSITION[_DBType] - 1) << 2 : 0;
				// LASTSEEN_POSITION_OFFSET = (LASTSEEN_POSITION[_DBType] != 0) ? (LASTSEEN_POSITION[_DBType] - 1) << 2 : 0;
				// THREAT_POSITION_OFFSET = (THREAT_POSITION[_DBType] != 0) ? (THREAT_POSITION[_DBType] - 1) << 2 : 0;
				
				// slightly different offset for reading by row
				COUNTRY_POSITION_OFFSET = (COUNTRY_POSITION[_DBType] != 0) ? (COUNTRY_POSITION[_DBType] - 2) << 2 : 0;
				REGION_POSITION_OFFSET = (REGION_POSITION[_DBType] != 0) ? (REGION_POSITION[_DBType] - 2) << 2 : 0;
				CITY_POSITION_OFFSET = (CITY_POSITION[_DBType] != 0) ? (CITY_POSITION[_DBType] - 2) << 2 : 0;
				ISP_POSITION_OFFSET = (ISP_POSITION[_DBType] != 0) ? (ISP_POSITION[_DBType] - 2) << 2 : 0;
				PROXYTYPE_POSITION_OFFSET = (PROXYTYPE_POSITION[_DBType] != 0) ? (PROXYTYPE_POSITION[_DBType] - 2) << 2 : 0;
				DOMAIN_POSITION_OFFSET = (DOMAIN_POSITION[_DBType] != 0) ? (DOMAIN_POSITION[_DBType] - 2) << 2 : 0;
				USAGETYPE_POSITION_OFFSET = (USAGETYPE_POSITION[_DBType] != 0) ? (USAGETYPE_POSITION[_DBType] - 2) << 2 : 0;
				ASN_POSITION_OFFSET = (ASN_POSITION[_DBType] != 0) ? (ASN_POSITION[_DBType] - 2) << 2 : 0;
				AS_POSITION_OFFSET = (AS_POSITION[_DBType] != 0) ? (AS_POSITION[_DBType] - 2) << 2 : 0;
				LASTSEEN_POSITION_OFFSET = (LASTSEEN_POSITION[_DBType] != 0) ? (LASTSEEN_POSITION[_DBType] - 2) << 2 : 0;
				THREAT_POSITION_OFFSET = (THREAT_POSITION[_DBType] != 0) ? (THREAT_POSITION[_DBType] - 2) << 2 : 0;
				
				
				COUNTRY_ENABLED = (COUNTRY_POSITION[_DBType] != 0) ? true : false;
				REGION_ENABLED = (REGION_POSITION[_DBType] != 0) ? true : false;
				CITY_ENABLED = (CITY_POSITION[_DBType] != 0) ? true : false;
				ISP_ENABLED = (ISP_POSITION[_DBType] != 0) ? true : false;
				PROXYTYPE_ENABLED = (PROXYTYPE_POSITION[_DBType] != 0) ? true : false;
				DOMAIN_ENABLED = (DOMAIN_POSITION[_DBType] != 0) ? true : false;
				USAGETYPE_ENABLED = (USAGETYPE_POSITION[_DBType] != 0) ? true : false;
				ASN_ENABLED = (ASN_POSITION[_DBType] != 0) ? true : false;
				AS_ENABLED = (AS_POSITION[_DBType] != 0) ? true : false;
				LASTSEEN_ENABLED = (LASTSEEN_POSITION[_DBType] != 0) ? true : false;
				THREAT_ENABLED = (THREAT_POSITION[_DBType] != 0) ? true : false;
				
				final MappedByteBuffer _IndexBuffer = InChannel.map(FileChannel.MapMode.READ_ONLY, _IndexBaseAddr - 1, _BaseAddr - _IndexBaseAddr); // reading indexes
				_IndexBuffer.order(ByteOrder.LITTLE_ENDIAN);
				int Pointer = 0;
				
				// read IPv4 index
				for (int x = 0; x < _IndexArrayIPv4.length; x++) {
					_IndexArrayIPv4[x][0] = _IndexBuffer.getInt(Pointer); // 4 bytes for from row
					_IndexArrayIPv4[x][1] = _IndexBuffer.getInt(Pointer + 4); // 4 bytes for to row
					Pointer += 8;
				}
				
				if (_IndexBaseAddrIPv6 > 0) {
					// read IPv6 index
					for (int x = 0; x < _IndexArrayIPv6.length; x++) {
						_IndexArrayIPv6[x][0] = _IndexBuffer.getInt(Pointer); // 4 bytes for from row
						_IndexArrayIPv6[x][1] = _IndexBuffer.getInt(Pointer + 4); // 4 bytes for to row
						Pointer += 8;
					}
				}
				
				if (_UseMemoryMappedFile) {
					CreateMappedBytes(InChannel);
				}
				else {
					DestroyMappedBytes();
				}
				LoadOK = true;
			}
		}
		catch(IOException Ex) {
			throw Ex;
		}
		finally {
			if (RF != null) {
				RF.close();
				RF = null;
			}
		}
		return LoadOK;
	}
	
/**
* This function initialize the component with the BIN file path and IO mode.
* @param DatabasePath Path to the BIN database file
* @param IOMode Default is file IO
* @return -1 if encounter error else 0
*/
	public int Open(String DatabasePath) throws IOException {
		return Open(DatabasePath, IOModes.IP2PROXY_FILE_IO);
	}
	
	public int Open(String DatabasePath, IOModes IOMode) throws IOException {
		if (_DBType == 0) {
			_IPDatabasePath = DatabasePath;
			
			if (IOMode == IOModes.IP2PROXY_MEMORY_MAPPED) {
				_UseMemoryMappedFile = true;
			}
			
			if (!LoadBIN()) {
				return -1;
			}
			else {
				return 0;
			}
		}
		else {
			return 0;
		}
	}
	
/**
* This function to query IP2Proxy data.
* @param IPAddress IP Address you wish to query
* @return IP2Proxy data
*/
	public ProxyResult ProxyQuery(String IPAddress) throws IOException {
		return ProxyQuery(IPAddress, Modes.ALL);
	}
	
	public ProxyResult ProxyQuery(String IPAddress, Modes Mode) throws IOException {
		ProxyResult Result = new ProxyResult();
		RandomAccessFile RF = null;
		// MappedByteBuffer Buf = null;
		ByteBuffer Buf = null;
		ByteBuffer DataBuf = null;
		
		try {
			if (IPAddress == null || IPAddress.length() == 0) {
				Result.Is_Proxy = -1;
				Result.Proxy_Type = MSG_INVALID_IP;
				Result.Country_Short = MSG_INVALID_IP;
				Result.Country_Long = MSG_INVALID_IP;
				Result.Region = MSG_INVALID_IP;
				Result.City = MSG_INVALID_IP;
				Result.ISP = MSG_INVALID_IP;
				Result.Domain = MSG_INVALID_IP;
				Result.Usage_Type = MSG_INVALID_IP;
				Result.ASN = MSG_INVALID_IP;
				Result.AS = MSG_INVALID_IP;
				Result.Last_Seen = MSG_INVALID_IP;
				Result.Threat = MSG_INVALID_IP;
				return Result;
			}
			
			BigInteger IPNo;
			int IndexAddr = 0;
			int ActualIPType = 0;
			int IPType = 0;
			int DBType = 0;
			int BaseAddr = 0;
			int DBColumn = 0;
			int ColumnSize = 0;
			int BufCapacity = 0;
			BigInteger MAX_IP_RANGE = BigInteger.ZERO;
			long RowOffset = 0;
			long RowOffset2 = 0;
			BigInteger[] BI;
			boolean OverCapacity = false;
			String[] RetArr;
			
			try {
				BI = IP2No(IPAddress);
				IPType = BI[0].intValue();
				IPNo = BI[1];
				ActualIPType = BI[2].intValue();
				if (ActualIPType == 6) {
					RetArr = ExpandIPv6(IPAddress, IPType);
					IPType = Integer.parseInt(RetArr[1]);
				}
			}
			catch(UnknownHostException Ex) {
				Result.Is_Proxy = -1;
				Result.Proxy_Type = MSG_INVALID_IP;
				Result.Country_Short = MSG_INVALID_IP;
				Result.Country_Long = MSG_INVALID_IP;
				Result.Region = MSG_INVALID_IP;
				Result.City = MSG_INVALID_IP;
				Result.ISP = MSG_INVALID_IP;
				Result.Domain = MSG_INVALID_IP;
				Result.Usage_Type = MSG_INVALID_IP;
				Result.ASN = MSG_INVALID_IP;
				Result.AS = MSG_INVALID_IP;
				Result.Last_Seen = MSG_INVALID_IP;
				Result.Threat = MSG_INVALID_IP;
				return Result;
			}
			
			long Pos = 0;
			long Low = 0;
			long High = 0;
			long Mid = 0;
			BigInteger IPFrom = BigInteger.ZERO;
			BigInteger IPTo = BigInteger.ZERO;
			
			// Read BIN if haven't done so
			if (_DBType == 0) {
				if (!LoadBIN()) { // problems reading BIN
					Result.Is_Proxy = -1;
					Result.Proxy_Type = MSG_MISSING_FILE;
					Result.Country_Short = MSG_MISSING_FILE;
					Result.Country_Long = MSG_MISSING_FILE;
					Result.Region = MSG_MISSING_FILE;
					Result.City = MSG_MISSING_FILE;
					Result.ISP = MSG_MISSING_FILE;
					Result.Domain = MSG_MISSING_FILE;
					Result.Usage_Type = MSG_MISSING_FILE;
					Result.ASN = MSG_MISSING_FILE;
					Result.AS = MSG_MISSING_FILE;
					Result.Last_Seen = MSG_MISSING_FILE;
					Result.Threat = MSG_MISSING_FILE;
					return Result;
				}
			}
			
			if (_UseMemoryMappedFile) {
				if ((_IPv4Buffer == null) || (_DBCountIPv6 > 0 && _IPv6Buffer == null) || (_MapDataBuffer == null)) {
					CreateMappedBytes();
				}
			}
			else {
				DestroyMappedBytes();
				RF = new RandomAccessFile(_IPDatabasePath, "r");
			}
			
			if (IPType == 4) { // IPv4
				MAX_IP_RANGE = MAX_IPV4_RANGE;
				High = _DBCount;
				
				if (_UseMemoryMappedFile) {
					// Buf = _IPv4Buffer;
					Buf = _IPv4Buffer.duplicate(); // this enables this thread to maintain its own position in a multi-threaded environment
					Buf.order(ByteOrder.LITTLE_ENDIAN);
					BufCapacity = Buf.capacity();
				}
				else {
					BaseAddr = _BaseAddr;
				}
				ColumnSize = _IPv4ColumnSize;
				
				IndexAddr = IPNo.shiftRight(16).intValue();
				Low = _IndexArrayIPv4[IndexAddr][0];
				High = _IndexArrayIPv4[IndexAddr][1];
			}
			else { // IPv6
				if (_DBCountIPv6 == 0) {
					Result.Is_Proxy = -1;
					Result.Proxy_Type = MSG_IPV6_UNSUPPORTED;
					Result.Country_Short = MSG_IPV6_UNSUPPORTED;
					Result.Country_Long = MSG_IPV6_UNSUPPORTED;
					Result.Region = MSG_IPV6_UNSUPPORTED;
					Result.City = MSG_IPV6_UNSUPPORTED;
					Result.ISP = MSG_IPV6_UNSUPPORTED;
					Result.Domain = MSG_IPV6_UNSUPPORTED;
					Result.Usage_Type = MSG_IPV6_UNSUPPORTED;
					Result.ASN = MSG_IPV6_UNSUPPORTED;
					Result.AS = MSG_IPV6_UNSUPPORTED;
					Result.Last_Seen = MSG_IPV6_UNSUPPORTED;
					Result.Threat = MSG_IPV6_UNSUPPORTED;
					return Result;
				}
				MAX_IP_RANGE = MAX_IPV6_RANGE;
				High = _DBCountIPv6;
				
				if (_UseMemoryMappedFile) {
					// Buf = _IPv6Buffer;
					Buf = _IPv6Buffer.duplicate(); // this enables this thread to maintain its own position in a multi-threaded environment
					Buf.order(ByteOrder.LITTLE_ENDIAN);
					BufCapacity = Buf.capacity();
				}
				else {
					BaseAddr = _BaseAddrIPv6;
				}
				ColumnSize = _IPv6ColumnSize;
				
				if (_IndexBaseAddrIPv6 > 0) {
					IndexAddr = IPNo.shiftRight(112).intValue();
					Low = _IndexArrayIPv6[IndexAddr][0];
					High = _IndexArrayIPv6[IndexAddr][1];
				}
			}
			
			if (IPNo.compareTo(MAX_IP_RANGE) == 0) IPNo = IPNo.subtract(BigInteger.ONE);
			
			while (Low <= High) {
				Mid = (long)((Low + High)/2);
				RowOffset = BaseAddr + (Mid * ColumnSize);
				RowOffset2 = RowOffset + ColumnSize;
				
				if (_UseMemoryMappedFile) {
					OverCapacity = (RowOffset2 >= BufCapacity);
				}
				
				IPFrom = Read32Or128(RowOffset, IPType, Buf, RF);
				IPTo = (OverCapacity) ? BigInteger.ZERO : Read32Or128(RowOffset2, IPType, Buf, RF);
				
				if (IPNo.compareTo(IPFrom) >= 0 && IPNo.compareTo(IPTo) < 0) {
					int Is_Proxy = -1;
					String Proxy_Type = MSG_NOT_SUPPORTED;
					String Country_Short = MSG_NOT_SUPPORTED;
					String Country_Long = MSG_NOT_SUPPORTED;
					String Region = MSG_NOT_SUPPORTED;
					String City = MSG_NOT_SUPPORTED;
					String ISP = MSG_NOT_SUPPORTED;
					String Domain = MSG_NOT_SUPPORTED;
					String Usage_Type = MSG_NOT_SUPPORTED;
					String ASN = MSG_NOT_SUPPORTED;
					String AS = MSG_NOT_SUPPORTED;
					String Last_Seen = MSG_NOT_SUPPORTED;
					String Threat = MSG_NOT_SUPPORTED;
					
					int FirstCol = 4; // IP From is 4 bytes
					if (IPType == 6) { // IPv6
						FirstCol = 16; // IPv6 is 16 bytes
						// RowOffset = RowOffset + 12; // coz below is assuming all columns are 4 bytes, so got 12 left to go to make 16 bytes total
					}
					
					// read the row here after the IP From column (remaining columns are all 4 bytes)
					int RowLen = ColumnSize - FirstCol;
					byte[] Row;
					Row = ReadRow(RowOffset + FirstCol, RowLen, Buf, RF);
					
					if (_UseMemoryMappedFile) {
						DataBuf = _MapDataBuffer.duplicate(); // this is to enable reading of a range of bytes in multi-threaded environment
						DataBuf.order(ByteOrder.LITTLE_ENDIAN);
					}
					
					if (PROXYTYPE_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.PROXY_TYPE || Mode == Modes.IS_PROXY) {
							// Proxy_Type = ReadStr(Read32(RowOffset + PROXYTYPE_POSITION_OFFSET, Buf, RF).longValue(), RF);
							Proxy_Type = ReadStr(Read32_Row(Row, PROXYTYPE_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (COUNTRY_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.COUNTRY_SHORT || Mode == Modes.COUNTRY_LONG || Mode == Modes.IS_PROXY) {
							// CountryPos = Read32(RowOffset + COUNTRY_POSITION_OFFSET, Buf, RF).longValue();
							Pos = Read32_Row(Row, COUNTRY_POSITION_OFFSET).longValue();
						}
						if (Mode == Modes.ALL || Mode == Modes.COUNTRY_SHORT || Mode == Modes.IS_PROXY) {
							// Country_Short = ReadStr(CountryPos, RF);
							Country_Short = ReadStr(Pos, DataBuf, RF);
						}
						if (Mode == Modes.ALL || Mode == Modes.COUNTRY_LONG) {
							// Country_Long = ReadStr(CountryPos + 3, RF);
							Country_Long = ReadStr(Pos + 3, DataBuf, RF);
						}
					}
					
					if (REGION_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.REGION) {
							// Region = ReadStr(Read32(RowOffset + REGION_POSITION_OFFSET, Buf, RF).longValue(), RF);
							Region = ReadStr(Read32_Row(Row, REGION_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (CITY_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.CITY) {
							// City = ReadStr(Read32(RowOffset + CITY_POSITION_OFFSET, Buf, RF).longValue(), RF);
							City = ReadStr(Read32_Row(Row, CITY_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (ISP_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.ISP) {
							// ISP = ReadStr(Read32(RowOffset + ISP_POSITION_OFFSET, Buf, RF).longValue(), RF);
							ISP = ReadStr(Read32_Row(Row, ISP_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (DOMAIN_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.DOMAIN) {
							// Domain = ReadStr(Read32(RowOffset + DOMAIN_POSITION_OFFSET, Buf, RF).longValue(), RF);
							Domain = ReadStr(Read32_Row(Row, DOMAIN_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (USAGETYPE_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.USAGE_TYPE) {
							// Usage_Type = ReadStr(Read32(RowOffset + USAGETYPE_POSITION_OFFSET, Buf, RF).longValue(), RF);
							Usage_Type = ReadStr(Read32_Row(Row, USAGETYPE_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (ASN_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.ASN) {
							// ASN = ReadStr(Read32(RowOffset + ASN_POSITION_OFFSET, Buf, RF).longValue(), RF);
							ASN = ReadStr(Read32_Row(Row, ASN_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (AS_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.AS) {
							// AS = ReadStr(Read32(RowOffset + AS_POSITION_OFFSET, Buf, RF).longValue(), RF);
							AS = ReadStr(Read32_Row(Row, AS_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (LASTSEEN_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.LAST_SEEN) {
							// Last_Seen = ReadStr(Read32(RowOffset + LASTSEEN_POSITION_OFFSET, Buf, RF).longValue(), RF);
							Last_Seen = ReadStr(Read32_Row(Row, LASTSEEN_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (THREAT_ENABLED) {
						if (Mode == Modes.ALL || Mode == Modes.THREAT) {
							// Threat = ReadStr(Read32(RowOffset + THREAT_POSITION_OFFSET, Buf, RF).longValue(), RF);
							Threat = ReadStr(Read32_Row(Row, THREAT_POSITION_OFFSET).longValue(), DataBuf, RF);
						}
					}
					
					if (Country_Short.equals("-") || Proxy_Type.equals("-")) {
						Is_Proxy = 0;
					}
					else {
						if (Proxy_Type.equals("DCH") || Proxy_Type.equals("SES")) {
							Is_Proxy = 2;
						}
						else {
							Is_Proxy = 1;
						}
					}
					
					Result.Is_Proxy = Is_Proxy;
					Result.Proxy_Type = Proxy_Type;
					Result.Country_Short = Country_Short;
					Result.Country_Long = Country_Long;
					Result.Region = Region;
					Result.City = City;
					Result.ISP = ISP;
					Result.Domain = Domain;
					Result.Usage_Type = Usage_Type;
					Result.ASN = ASN;
					Result.AS = AS;
					Result.Last_Seen = Last_Seen;
					Result.Threat = Threat;
					return Result;
				}
				else {
					if (IPNo.compareTo(IPFrom) < 0) {
						High = Mid - 1;
					}
					else {
						Low = Mid + 1;
					}
				}
			}
			Result.Is_Proxy = -1;
			Result.Proxy_Type = MSG_INVALID_IP;
			Result.Country_Short = MSG_INVALID_IP;
			Result.Country_Long = MSG_INVALID_IP;
			Result.Region = MSG_INVALID_IP;
			Result.City = MSG_INVALID_IP;
			Result.ISP = MSG_INVALID_IP;
			Result.Domain = MSG_INVALID_IP;
			Result.Usage_Type = MSG_INVALID_IP;
			Result.ASN = MSG_INVALID_IP;
			Result.AS = MSG_INVALID_IP;
			Result.Last_Seen = MSG_INVALID_IP;
			Result.Threat = MSG_INVALID_IP;
			return Result;
		}
		catch(IOException Ex) {
			throw Ex;
		}
		finally {
			if (RF != null) {
				RF.close();
				RF = null;
			}
		}
	}
	
	private String[] ExpandIPv6(final String IP, final int IPType) {
		final String Tmp = "0000:0000:0000:0000:0000:";
		final String PadMe = "0000";
		final long HexOffset = 0xFF;
		String IP2 = IP.toUpperCase();
		String RetType = String.valueOf(IPType);
		
		if (IPType == 4) {
			if (Pattern4.matcher(IP2).matches()) {
				IP2 = IP2.replaceAll("::", Tmp);
			}
			else {
				Matcher Mat = Pattern5.matcher(IP2);
				
				if (Mat.matches()) {
					String Match = Mat.group(1);
					
					String[] Arr = Match.replaceAll("^:+", "").replaceAll(":+$", "").split(":");
					
					int Len = Arr.length;
					
					StringBuilder Bf = new StringBuilder(32);
					for (int x = 0; x < Len; x++) {
						String Unpadded = Arr[x];
						Bf.append(PadMe.substring(Unpadded.length()) + Unpadded);
					}
					long Tmp2 = new BigInteger(Bf.toString(), 16).longValue();
					
					long[] Bytes = {0, 0, 0, 0}; // using long in place of bytes due to 2's complement signed issue
					
					for (int x = 0; x < 4; x++) {
						Bytes[x] = Tmp2 & HexOffset;
						Tmp2 = Tmp2 >> 8;
					}
					
					IP2 = IP2.replaceAll(Match + "$", ":" + Bytes[3] + "." + Bytes[2] + "." + Bytes[1] + "." + Bytes[0]);
					IP2 = IP2.replaceAll("::", Tmp);
				}
				else {
				}
			}
		}
		else if (IPType == 6) {
			if (IP2.equals("::")) {
				IP2 = IP2 + "0.0.0.0";
				IP2 = IP2.replaceAll("::", Tmp + "FFFF:");
				RetType = "4";
			}
			else {
				Matcher Mat = Pattern4.matcher(IP2);
				if (Mat.matches()) {
					String V6Part = Mat.group(1);
					String V4Part = Mat.group(2);
					
					String[] V4Arr = V4Part.split("\\.");
					int[] V4IntArr = new int[4];
					
					int Len = V4IntArr.length;
					for (int x = 0; x < Len; x++) {
						V4IntArr[x] = Integer.parseInt(V4Arr[x]);
					}
					int Part1 = (V4IntArr[0] << 8) + V4IntArr[1];
					int Part2 = (V4IntArr[2] << 8) + V4IntArr[3];
					String Part1Hex = Integer.toHexString(Part1);
					String Part2Hex = Integer.toHexString(Part2);
					
					StringBuilder Bf = new StringBuilder(V6Part.length() + 9);
					Bf.append(V6Part);
					Bf.append(PadMe.substring(Part1Hex.length()));
					Bf.append(Part1Hex);
					Bf.append(":");
					Bf.append(PadMe.substring(Part2Hex.length()));
					Bf.append(Part2Hex);
					
					IP2 = Bf.toString().toUpperCase();
					
					String[] Arr = IP2.split("::");
					
					String[] LeftSide = Arr[0].split(":");
					
					StringBuilder Bf2 = new StringBuilder(40);
					StringBuilder Bf3 = new StringBuilder(40);
					StringBuilder Bf4 = new StringBuilder(40);
					
					Len = LeftSide.length;
					int TotalSegments = 0;
					for (int x = 0; x < Len; x++) {
						if (LeftSide[x].length() > 0) {
							TotalSegments++;
							Bf2.append(PadMe.substring(LeftSide[x].length()));
							Bf2.append(LeftSide[x]);
							Bf2.append(":");
						}
					}
					
					if (Arr.length > 1) {
						String[] RightSide = Arr[1].split(":");
						
						Len = RightSide.length;
						for (int x = 0; x < Len; x++) {
							if (RightSide[x].length() > 0) {
								TotalSegments++;
								Bf3.append(PadMe.substring(RightSide[x].length()));
								Bf3.append(RightSide[x]);
								Bf3.append(":");
							}
						}
					}
					
					int TotalSegmentsLeft = 8 - TotalSegments;
					
					if (TotalSegmentsLeft == 6) {
						for (int x = 1; x < TotalSegmentsLeft; x++) {
							Bf4.append(PadMe);
							Bf4.append(":");
						}
						Bf4.append("FFFF:");
						Bf4.append(V4Part);
						RetType = "4";
						IP2 = Bf4.toString();
					}
					else {
						for (int x = 0; x < TotalSegmentsLeft; x++) {
							Bf4.append(PadMe);
							Bf4.append(":");
						}
						Bf2.append(Bf4).append(Bf3);
						IP2 = Bf2.toString().replaceAll(":$", "");
					}
					
				}
				else {
					Matcher Mat2 = Pattern6.matcher(IP2);
					
					if (Mat2.matches()) {
						String Match = Mat2.group(1);
						String[] Arr = Match.replaceAll("^:+", "").replaceAll(":+$", "").split(":");
						
						int Len = Arr.length;
						StringBuilder Bf = new StringBuilder(32);
						for (int x = 0; x < Len; x++) {
							String Unpadded = Arr[x];
							Bf.append(PadMe.substring(Unpadded.length()) + Unpadded);
						}
						
						long Tmp2 = new BigInteger(Bf.toString(), 16).longValue();
						
						long[] Bytes = {0, 0, 0, 0}; // using long in place of bytes due to 2's complement signed issue
						
						for (int x = 0; x < 4; x++) {
							Bytes[x] = Tmp2 & HexOffset;
							Tmp2 = Tmp2 >> 8;
						}
						
						IP2 = IP2.replaceAll(Match + "$", ":" + Bytes[3] + "." + Bytes[2] + "." + Bytes[1] + "." + Bytes[0]);
						IP2 = IP2.replaceAll("::", Tmp + "FFFF:");
						RetType = "4";
					}
					else {
						String[] Arr = IP2.split("::");
						
						String[] LeftSide = Arr[0].split(":");
						
						StringBuilder Bf2 = new StringBuilder(40);
						StringBuilder Bf3 = new StringBuilder(40);
						StringBuilder Bf4 = new StringBuilder(40);
						
						int Len = LeftSide.length;
						int TotalSegments = 0;
						for (int x = 0; x < Len; x++) {
							if (LeftSide[x].length() > 0) {
								TotalSegments++;
								Bf2.append(PadMe.substring(LeftSide[x].length()));
								Bf2.append(LeftSide[x]);
								Bf2.append(":");
							}
						}
						
						if (Arr.length > 1) {
							String[] RightSide = Arr[1].split(":");
							
							Len = RightSide.length;
							for (int x = 0; x < Len; x++) {
								if (RightSide[x].length() > 0) {
									TotalSegments++;
									Bf3.append(PadMe.substring(RightSide[x].length()));
									Bf3.append(RightSide[x]);
									Bf3.append(":");
								}
							}
						}
						
						int TotalSegmentsLeft = 8 - TotalSegments;
						
						for (int x = 0; x < TotalSegmentsLeft; x++) {
							Bf4.append(PadMe);
							Bf4.append(":");
						}
						
						Bf2.append(Bf4).append(Bf3);
						IP2 = Bf2.toString().replaceAll(":$", "");
					}
				}
			}
		}
		
		String[] RetArr = {IP2, RetType};
		
		return RetArr;
	}
	
	private void Reverse(byte[] Arr) {
		if (Arr == null) {
			return;
		}
		int i = 0;
		int j = Arr.length - 1;
		byte tmp;
		while (j > i) {
			tmp = Arr[j];
			Arr[j] = Arr[i];
			Arr[i] = tmp;
			j--;
			i++;
		}
	}
	
	private byte[] ReadRow(final long Position, final long MyLen, final ByteBuffer Buf, final RandomAccessFile RH) throws IOException {
		byte[] Row = new byte[(int)MyLen];
		if (_UseMemoryMappedFile) {
			Buf.position((int)Position);
			Buf.get(Row, (int)0, (int)MyLen);
		}
		else {
			RH.seek(Position - 1);
			RH.read(Row, (int)0, (int)MyLen);
		}
		return Row;
	}
	
	private BigInteger Read32Or128(final long Position, final int IPType, final ByteBuffer Buf, final RandomAccessFile RH) throws IOException {
		if (IPType == 4) {
			return Read32(Position, Buf, RH);
		}
		else if (IPType == 6) {
			return Read128(Position, Buf, RH); // only IPv6 will run this
		}
		return BigInteger.ZERO;
	}
	
	private BigInteger Read128(final long Position, final ByteBuffer Buf, final RandomAccessFile RH) throws IOException {
		BigInteger RetVal = BigInteger.ZERO;
		final int BSize = 16;
		byte Bytes[] = new byte[BSize];
		
		if (_UseMemoryMappedFile) {
			Buf.position((int)Position);
			Buf.get(Bytes, (int)0, BSize);
		}
		else {
			RH.seek(Position - 1);
			RH.read(Bytes, (int)0, BSize);
		}
		Reverse(Bytes);
		RetVal = new BigInteger(1, Bytes);
		return RetVal;
	}
	
	private BigInteger Read32_Row(byte[] Row, final int From) throws IOException {
		final int Len = 4; // 4 bytes
		byte Bytes[] = new byte[Len];
		System.arraycopy(Row, From, Bytes, (int)0, Len);
		Reverse(Bytes);
		return new BigInteger(1, Bytes);
	}
	
	private BigInteger Read32(final long Position, final ByteBuffer Buf, final RandomAccessFile RH) throws IOException {
		if (_UseMemoryMappedFile) {
			// simulate unsigned int by using long
			return BigInteger.valueOf(Buf.getInt((int)Position) & 0xffffffffL); // use absolute offset to be thread-safe
		}
		else {
			final int BSize = 4;
			RH.seek(Position - 1);
			byte Bytes[] = new byte[BSize];
			RH.read(Bytes, (int)0, BSize);
			Reverse(Bytes);
			return new BigInteger(1, Bytes);
		}
	}
	
	private String ReadStr(long Position, final ByteBuffer Buf, final RandomAccessFile RH) throws IOException {
		final int Size;
		byte[] Bytes = null;
		
		if (_UseMemoryMappedFile) {
			Position = Position - _MapDataOffset; // position stored in BIN file is for full file, not just the mapped data segment, so need to minus
			Size = _MapDataBuffer.get((int)Position); // use absolute offset to be thread-safe
			
			try {
				Bytes = new byte[Size];
				Buf.position((int)Position + 1);
				Buf.get(Bytes, (int)0, Size);
			}
			catch (NegativeArraySizeException e) {
				return null;
			}
		}
		else {
			RH.seek(Position);
			Size = RH.read();
			try {
				Bytes = new byte[Size];
				RH.read(Bytes, (int)0, Size);
			}
			catch (NegativeArraySizeException e) {
				return null;
			}
		}
		
		String S = new String(Bytes);
		return S;
	}
	
	private BigInteger[] IP2No(String IP) throws UnknownHostException {
		BigInteger A1 = BigInteger.ZERO;
		BigInteger A2 = BigInteger.ZERO;
		BigInteger A3 = new BigInteger("4");
		
		if (Pattern1.matcher(IP).matches()) { // should be IPv4
			A1 = new BigInteger("4");
			A2 = new BigInteger(String.valueOf(IPv4No(IP)));
		}
		else if (Pattern2.matcher(IP).matches() || Pattern3.matcher(IP).matches() || Pattern7.matcher(IP).matches()) {
			throw new UnknownHostException();
		}
		else {
			A3 = new BigInteger("6");
			final InetAddress IA = InetAddress.getByName(IP);
			final byte Bytes[] = IA.getAddress();
			
			String IPType = "0"; // BigInteger needs String in the constructor
			
			if (IA instanceof Inet6Address) {
				IPType = "6";
			}
			else if (IA instanceof Inet4Address) { // this will run in cases of IPv4-mapped IPv6 addresses
				IPType = "4";
			}
			A2 = new BigInteger(1, Bytes);
			
			if (A2.compareTo(FROM_6TO4) >= 0 && A2.compareTo(TO_6TO4) <= 0) {
				// 6to4 so need to remap to ipv4
				IPType = "4";
				A2 = A2.shiftRight(80);
				A2 = A2.and(LAST_32BITS);
				A3 = new BigInteger("4");
			}
			else if (A2.compareTo(FROM_TEREDO) >= 0 && A2.compareTo(TO_TEREDO) <= 0) {
				// Teredo so need to remap to ipv4
				IPType = "4";
				A2 = A2.not();
				A2 = A2.and(LAST_32BITS);
				A3 = new BigInteger("4");
			}
			A1 = new BigInteger(IPType);
		}
		BigInteger[] BI = new BigInteger[] { A1, A2, A3 };
		
		return BI;
	}
	
	private long IPv4No(final String IP) {
		final String[] IPs = IP.split("\\.");
		long RetVal = 0;
		long IPLong = 0;
		for (int x = 3; x >= 0; x--) {
			IPLong = Long.parseLong(IPs[3 - x]);
			RetVal |= IPLong << (x << 3);
		}
		return RetVal;
	}
}
