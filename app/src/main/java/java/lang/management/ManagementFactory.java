package java.lang.management;

import java.util.List;
import java.util.Map;

public class ManagementFactory implements RuntimeMXBean {

    private static final ManagementFactory instance = new ManagementFactory();

    public static RuntimeMXBean getRuntimeMXBean(){
        return instance;
    }

    @Override
    public String getName() {
        return "Android@0";
    }

    @Override
    public String getVmName() {
        return null;
    }

    @Override
    public String getVmVendor() {
        return null;
    }

    @Override
    public String getVmVersion() {
        return null;
    }

    @Override
    public String getSpecName() {
        return null;
    }

    @Override
    public String getSpecVendor() {
        return null;
    }

    @Override
    public String getSpecVersion() {
        return null;
    }

    @Override
    public String getManagementSpecVersion() {
        return null;
    }

    @Override
    public String getClassPath() {
        return null;
    }

    @Override
    public String getLibraryPath() {
        return null;
    }

    @Override
    public boolean isBootClassPathSupported() {
        return false;
    }

    @Override
    public String getBootClassPath() {
        return null;
    }

    @Override
    public List<String> getInputArguments() {
        return null;
    }

    @Override
    public long getUptime() {
        return 0;
    }

    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public Map<String, String> getSystemProperties() {
        return null;
    }
}
