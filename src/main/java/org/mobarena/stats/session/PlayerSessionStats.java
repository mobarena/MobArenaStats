package org.mobarena.stats.session;

import java.time.Instant;
import java.util.UUID;

public class PlayerSessionStats {

    public final UUID sessionId;
    public final UUID playerId;
    public final String playerName;

    public String className;

    public Instant joinTime;
    public Instant readyTime;
    public Instant leaveTime;
    public Instant deathTime;

    public int kills;
    public int dmgDone;
    public int dmgTaken;
    public int swings;
    public int hits;
    public int lastWave;

    public PlayerConclusion conclusion;

    public PlayerSessionStats(
        UUID sessionId,
        UUID playerId,
        String playerName
    ) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.playerName = playerName;
    }

}
