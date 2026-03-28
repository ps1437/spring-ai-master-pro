package com.syscho.ai.tools.systeminfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.List;

@Slf4j
@Component
public class SystemInfoTool {

    private static final long MB = 1024 * 1024;
    private static final long GB = 1024 * MB;

    private final SystemInfo si = new SystemInfo();
    private final HardwareAbstractionLayer hw = si.getHardware();
    private final OperatingSystem os = si.getOperatingSystem();


    @Tool(name = "getCpuInfo",
            description = "Get CPU details: name, physical cores, logical cores, and current load percentage")
    public String getCpuInfo() {
        log.info("[tool=getCpuInfo]");

        CentralProcessor cpu = hw.getProcessor();
        double[] loadTicks = new double[]{cpu.getSystemCpuLoadBetweenTicks(cpu.getSystemCpuLoadTicks())};
        double load = loadTicks[0] * 100;

        return "CPU: " + cpu.getProcessorIdentifier().getName()
                + " | Physical cores: " + cpu.getPhysicalProcessorCount()
                + " | Logical cores: " + cpu.getLogicalProcessorCount()
                + " | Current load: " + String.format("%.1f", load) + "%";
    }

    @Tool(name = "getMemoryInfo",
            description = "Get RAM details: total memory, used memory, available memory in MB")
    public String getMemoryInfo() {
        log.info("[tool=getMemoryInfo]");

        GlobalMemory mem = hw.getMemory();
        long total = mem.getTotal() / MB;
        long available = mem.getAvailable() / MB;
        long used = total - available;
        double usedPct = (used * 100.0) / total;

        return "Memory total: " + total + " MB"
                + " | Used: " + used + " MB (" + String.format("%.1f", usedPct) + "%)"
                + " | Available: " + available + " MB";
    }


    @Tool(name = "getDiskInfo",
            description = "Get disk / file system details: mount point, total size, used space, free space in GB")
    public String getDiskInfo() {
        log.info("[tool=getDiskInfo]");

        FileSystem fs = os.getFileSystem();
        List<OSFileStore> stores = fs.getFileStores();

        if (stores.isEmpty()) return "No file stores found.";

        StringBuilder sb = new StringBuilder("Disk info:\n");
        for (OSFileStore store : stores) {
            long total = store.getTotalSpace() / GB;
            long free = store.getUsableSpace() / GB;
            long used = total - free;
            double usedPct = total > 0 ? (used * 100.0) / total : 0;

            sb.append("  ").append(store.getMount())
                    .append(" | Total: ").append(total).append(" GB")
                    .append(" | Used: ").append(used).append(" GB (")
                    .append(String.format("%.1f", usedPct)).append("%)")
                    .append(" | Free: ").append(free).append(" GB")
                    .append("\n");
        }
        return sb.toString();
    }


    @Tool(name = "getOsInfo",
            description = "Get operating system details: name, version, architecture, uptime, running process count")
    public String getOsInfo() {
        log.info("[tool=getOsInfo]");

        long uptimeSec = os.getSystemUptime();
        long hours = uptimeSec / 3600;
        long minutes = (uptimeSec % 3600) / 60;

        return "OS: " + os.getFamily() + " " + os.getVersionInfo()
                + " | Arch: " + System.getProperty("os.arch")
                + " | Uptime: " + hours + "h " + minutes + "m"
                + " | Processes: " + os.getProcessCount()
                + " | Threads: " + os.getThreadCount();
    }


    @Tool(name = "getSystemHealthSummary",
            description = "Get a full system health summary including CPU, memory, disk and OS info in one call")
    public String getSystemHealthSummary() {
        log.info("[tool=getSystemHealthSummary]");

        return """
                === System Health Summary ===
                %s
                %s
                %s
                %s
                """.formatted(getCpuInfo(), getMemoryInfo(), getDiskInfo(), getOsInfo());
    }
}