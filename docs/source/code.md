# IP2Proxy Java API

## IP2Proxy Class
```{py:class} IP2Proxy()
Initiate IP2Proxy class.
```
```{py:function} Open(binPath)
Load the IP2Proxy BIN database for lookup.

:param String binPath: (Required) The file path links to IP2Proxy BIN databases.
```

```{py:function} Close()
Close and clean up the file pointer.
```

```{py:function} GetPackageVersion()
Return the database's type, 1 to 12 respectively for PX1 to PX12. Please visit https://www.ip2location.com/databases/ip2proxy for details.

:return: Returns the package version.
:rtype: string
```

```{py:function} GetModuleVersion()
Return the version of module.

:return: Returns the module version.
:rtype: string
```

```{py:function} GetDatabaseVersion()
Return the database's compilation date as a string of the form 'YYYY-MM-DD'.

:return: Returns the database version.
:rtype: string
```

```{py:function} GetAll(ipAddress)
Retrieve geolocation information for an IP address.

:param String ipAddress: (Required) The IP address (IPv4 or IPv6).
:return: Returns the geolocation information in array. Refer below table for the fields avaliable in the array
:rtype: array

**RETURN FIELDS**

| Field Name       | Description                                                  |
| ---------------- | ------------------------------------------------------------ |
| Country_Short    |     Two-character country code based on ISO 3166. |
| Country_Long    |     Country name based on ISO 3166. |
| Region     |     Region or state name. |
| City       |     City name. |
| ISP            |     Internet Service Provider or company\'s name. |
| Domain         |     Internet domain name associated with IP address range. |
| Usage_Type      |     Usage type classification of ISP or company. |
| ASN            |     Autonomous system number (ASN). |
| AS             |     Autonomous system (AS) name. |
| Last_Seen       |     Proxy last seen in days. |
| Threat         |     Security threat reported. |
| Proxy_Type      |     Type of proxy. |
| Provider       |     Name of VPN provider if available. |
| Fraud_Score       |     Potential risk score (0 - 99) associated with IP address. |
```