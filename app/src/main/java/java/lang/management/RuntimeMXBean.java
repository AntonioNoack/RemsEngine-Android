//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package java.lang.management;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface RuntimeMXBean extends PlatformManagedObject {

    String getName();

    String getVmName();

    String getVmVendor();

    String getVmVersion();

    String getSpecName();

    String getSpecVendor();

    String getSpecVersion();

    String getManagementSpecVersion();

    String getClassPath();

    String getLibraryPath();

    boolean isBootClassPathSupported();

    String getBootClassPath();

    List<String> getInputArguments();

    long getUptime();

    long getStartTime();

    Map<String, String> getSystemProperties();
}
