package com.syscho.ai.skills.web;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class TimeService {

    public ZonedDateTime now(String zone) {
        ZoneId zid = zone == null || zone.isBlank() ? ZoneId.systemDefault() : ZoneId.of(zone);
        return ZonedDateTime.now(zid);
    }

    /**
     * Convert a time from one zone to another.
     * time may be:
     * - ISO date-time (yyyy-MM-ddTHH:mm[:ss])
     * - local time like HH:mm (assumes today in fromZone)
     * - empty/null -> uses current time in fromZone
     */
    public ZonedDateTime convert(String time, String fromZone, String toZone) {
        ZoneId from = fromZone == null || fromZone.isBlank() ? ZoneId.systemDefault() : ZoneId.of(fromZone);
        ZoneId to = toZone == null || toZone.isBlank() ? ZoneId.systemDefault() : ZoneId.of(toZone);

        ZonedDateTime source;
        if (time == null || time.isBlank()) {
            source = ZonedDateTime.now(from);
        } else {
            // try parse ISO
            try {
                LocalDateTime ldt = LocalDateTime.parse(time);
                source = ldt.atZone(from);
            } catch (Exception e1) {
                try {
                    LocalTime lt = LocalTime.parse(time);
                    source = LocalDate.now(from).atTime(lt).atZone(from);
                } catch (Exception e2) {
                    // fallback to now
                    source = ZonedDateTime.now(from);
                }
            }
        }

        return source.withZoneSameInstant(to);
    }

    public String format(ZonedDateTime zdt, String pattern) {
        DateTimeFormatter fmt = pattern == null || pattern.isBlank()
                ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)
                : DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return zdt.format(fmt);
    }
}
