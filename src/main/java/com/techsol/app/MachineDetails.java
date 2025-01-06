package com.techsol.app;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class MachineDetails {

    public static String getSystemMemoryDetails() {
        StringBuilder details = new StringBuilder();

        // Operating System details
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        
        // System-level memory
        long totalPhysicalMemory = osBean.getTotalMemorySize();
        long freePhysicalMemory = osBean.getFreeMemorySize();
        long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;

        long totalSwapSpace = osBean.getTotalSwapSpaceSize();
        long freeSwapSpace = osBean.getFreeSwapSpaceSize();
        long usedSwapSpace = totalSwapSpace - freeSwapSpace;

        details.append("System Memory Details:\n");
        details.append("  Total Physical Memory: ").append(formatBytes(totalPhysicalMemory)).append("\n");
        details.append("  Free Physical Memory: ").append(formatBytes(freePhysicalMemory)).append("\n");
        details.append("  Used Physical Memory: ").append(formatBytes(usedPhysicalMemory)).append("\n");

        details.append("Swap Space Details:\n");
        details.append("  Total Swap Space: ").append(formatBytes(totalSwapSpace)).append("\n");
        details.append("  Free Swap Space: ").append(formatBytes(freeSwapSpace)).append("\n");
        details.append("  Used Swap Space: ").append(formatBytes(usedSwapSpace)).append("\n");

        // CPU Details
        double systemCpuLoad = osBean.getCpuLoad() * 100;
        double processCpuLoad = osBean.getProcessCpuLoad() * 100;

        details.append("CPU Details:\n");
        details.append("  System CPU Load: ").append(String.format("%.2f%%", systemCpuLoad)).append("\n");
        details.append("  Process CPU Load: ").append(String.format("%.2f%%", processCpuLoad)).append("\n");

        return details.toString();
    }

    private static String formatBytes(long bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;

        return String.format("%d bytes (%.2f KB, %.2f MB, %.2f GB)", bytes, kb, mb, gb);
    }

    public static void main(String[] args) {
        System.out.println(getSystemMemoryDetails());
    }
}
