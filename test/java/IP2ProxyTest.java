import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.*;
import java.io.*;

import com.ip2proxy.*;

class IP2ProxyTest {
    private static IP2Proxy Proxy;
    private static ProxyResult All;
    private static String binfile = "IP2PROXY-LITE-PX1.BIN";
    private static String binfilepath;
    private static byte[] binFileBytes;
    private static String ip = "8.8.8.8";

    @BeforeAll
    static void Setup() throws IOException {
        Path binpath = Paths.get("src", "test", "resources", binfile);
        binfilepath = binpath.toFile().getAbsolutePath();
        binFileBytes = Files.readAllBytes(binpath);
    }

    @BeforeEach
    void Init() {
        Proxy = new IP2Proxy();
    }

    @Test
    void TestOpenException() {
        assertThrows(IOException.class, () -> {
            Proxy.Open("dummy.bin");
        });

        assertThrows(NullPointerException.class, () -> {
            Proxy.Open((byte[])null);
        });
    }

    @Test
    void TestQueryIsProxy() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Is_Proxy, 0);
        }
    }

    @Test
    void TestQueryProxyType() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Proxy_Type, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryCountryShort() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Country_Short, "-");
        }
    }

    @Test
    void TestQueryCountryLong() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Country_Long, "-");
        }
    }

    @Test
    void TestQueryRegion() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Region, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryCity() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.City, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryISP() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.ISP, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryDomain() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Domain, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryUsageType() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Usage_Type, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryASN() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.ASN, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryAS() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.AS, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryLastSeen() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Last_Seen, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryThreat() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Threat, "NOT SUPPORTED");
        }
    }

    @Test
    void TestQueryProvider() throws IOException {
        if (Proxy.Open(binfilepath) == 0) {
            All = Proxy.GetAll(ip);
            assertEquals(All.Provider, "NOT SUPPORTED");
        }
    }

    @AfterEach
    void TearDown() {
        Proxy.Close();
        Proxy = null;
    }
}


