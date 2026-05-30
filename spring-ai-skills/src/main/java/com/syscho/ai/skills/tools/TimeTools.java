package com.syscho.ai.skills.tools;

import com.syscho.ai.skills.web.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * TimeTools — small tool exposed to the AI runtime via SkillsTool.
 *
 * Use cases:
 * - `now(zone)` — return the current time in the given IANA `zone` (e.g. "Asia/Kolkata").
 * - `convert(time, fromZone, toZone)` — convert a time between zones. `time` may be:
 *    - ISO date-time like `2026-05-30T20:00`
 *    - local time like `20:00` (assumes today in `fromZone`)
 *    - empty/null to use the current time in `fromZone`.
 *
 * The methods return a human-friendly formatted timestamp ("yyyy-MM-dd HH:mm:ss z").
 *
 * Registration: this bean is registered with the agent tooling in `AgentConfig` so the
 * model can call it when instructed by the system prompt.
 */
@Component
@RequiredArgsConstructor
public class TimeTools {

    private final TimeService timeService;

    /**
     * Return the current time in the given zone as a formatted string.
     * Examples:
     * - now("UTC") -> "2026-05-30 07:30:12 UTC"
     * - now("Asia/Kolkata") -> "2026-05-30 13:00:12 IST"
     *
     * @param zone IANA timezone id (optional). If null/blank, server default is used.
     * @return formatted timestamp string
     */


    @Tool(
            name = "now",
            description = "Return current time in the given IANA timezone. Defaults to UTC when zone is omitted.")
    public String now(@ToolParam(description = "IANA timezone id, e.g. 'Asia/Kolkata' (optional)") String zone) {
        String z = (zone == null || zone.isBlank()) ? "UTC" : zone;
        ZonedDateTime zdt = timeService.now(z);
        return timeService.format(zdt, null);
    }

    /**
     * Convert a time from one timezone to another and return a formatted result.
     * Examples:
     * - convert("20:00", "Asia/Kolkata", "America/New_York") -> "2026-05-30 10:30:00 EDT"
     * - convert(null, "UTC", "Asia/Kolkata") -> current time in Asia/Kolkata
     *
     * @param time     time string (ISO or local time like HH:mm) or null -> now
     * @param fromZone source IANA timezone id (optional)
     * @param toZone   target IANA timezone id (optional)
     * @return formatted timestamp string in target zone
     */
    @Tool(
            name = "convert",
            description = "Convert a time from one IANA timezone to another.\n" +
                    "time may be ISO datetime or local time like HH:mm; empty -> now. Defaults to UTC when zones omitted.")
    public String convert(
            @ToolParam(description = "Time string (ISO or HH:mm). Null/empty uses current time in fromZone") String time,
            @ToolParam(description = "Source IANA timezone, e.g. 'Asia/Kolkata' (optional, defaults to UTC)") String fromZone,
            @ToolParam(description = "Target IANA timezone, e.g. 'America/New_York' (optional, defaults to UTC)") String toZone) {
        String f = (fromZone == null || fromZone.isBlank()) ? "UTC" : fromZone;
        String t = (toZone == null || toZone.isBlank()) ? "UTC" : toZone;
        ZonedDateTime zdt = timeService.convert(time, f, t);
        return timeService.format(zdt, null);
    }
}
