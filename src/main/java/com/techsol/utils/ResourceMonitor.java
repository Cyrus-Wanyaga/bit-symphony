package com.techsol.utils;

import java.lang.management.ManagementFactory;
import java.util.Properties;

public class ResourceMonitor {
    private String JVMVersion;
    private String OS;
    private String osArch;
    private String availableProcessors;
    private String usedMemory;
    private String maxMemory;
    private String freeMemory;

    public ResourceMonitor() {
        Properties properties = System.getProperties();
        JVMVersion = properties.getProperty("java.version");
        OS = properties.getProperty("os.name");
        osArch = properties.getProperty("os.arch");
        availableProcessors = Integer.toString(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
        Runtime runtime = Runtime.getRuntime();
        freeMemory = Long.toString(runtime.freeMemory());
        usedMemory = Long.toString(runtime.totalMemory() - Long.valueOf(freeMemory));
        maxMemory = Long.toString(runtime.maxMemory());
    }

    public String getJVMVersion() {
        return JVMVersion;
    }

    public void setJVMVersion(String jVMVersion) {
        JVMVersion = jVMVersion;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String oS) {
        OS = oS;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(String availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public String getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(String usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(String maxMemory) {
        this.maxMemory = maxMemory;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
    }

}
