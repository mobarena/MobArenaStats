package org.mobarena.stats.session;

import java.time.Instant;
import java.util.UUID;

public class SessionStats {

    public final UUID sessionId;
    public final String arenaSlug;

    public Instant startTime;
    public Instant endTime;

    public int lastWave;

    public SessionConclusion conclusion;

    public SessionStats(UUID sessionId, String arenaSlug) {
        this.sessionId = sessionId;
        this.arenaSlug = arenaSlug;
    }

}
