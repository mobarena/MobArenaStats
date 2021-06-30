package org.mobarena.stats.store;

import org.junit.jupiter.api.Test;
import org.mobarena.stats.session.PlayerConclusion;
import org.mobarena.stats.session.PlayerSessionStats;
import org.mobarena.stats.session.Session;
import org.mobarena.stats.session.SessionConclusion;
import org.mobarena.stats.session.SessionStats;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Generic stats store integration test class.
 * <p>
 * For stores with complete implementations, creating a dervied test class
 * that follows the naming conventions of the Maven Failsafe Plugin will
 * result in the tests being run against the store during the integration
 * test phase.
 * <p>
 * Derived classes must implement the abstract {@link #getStore()} method,
 * which provides the parent class with a test subject.
 *
 * @see <a href="https://maven.apache.org/surefire/maven-failsafe-plugin/examples/inclusion-exclusion.html">Maven Failsafe Plugin naming conventions</a>
 */
public abstract class StatsStoreIT {

    /**
     * The template method that delivers a store instance for use in all
     * of the tests in this class. Called at the beginning of every test,
     * this method is expected to return the same instance throughout the
     * entire test run to save on time.
     *
     * @return a StatsStore instance
     */
    public abstract StatsStore getStore();

    /**
     * A very basic test that saves a session and deletes is afterwards.
     * <p>
     * Humble but important, if this test succeeds, writes should work just
     * fine for the given database implementation.
     */
    @Test
    void simpleSessionSaveAndDelete() throws Exception {
        StatsStore subject = getStore();

        // Player
        UUID id = UUID.fromString("deadbeef-ea75-dead-babe-deadbeef0001");
        String name = "alice";

        // Create and save a session
        UUID sessionId = UUID.fromString("cafebabe-ea75-dead-beef-deadbabe0001");
        String arenaSlug = "castle";
        Session session = new Session(sessionId, arenaSlug);
        set(session, 300, 23, SessionConclusion.DEFEAT);
        set(session, id, name, "tank", -59, 0, null, 672, 3, 6, PlayerConclusion.DEFEAT);
        subject.save(session);

        // Delete the session again
        subject.delete(sessionId);
    }

    /**
     * Two players join an arena, play different classes, and produce very
     * different results.
     * <p>
     * The goal of this test is to ensure that the session is captured "for"
     * both players, and that the "globals" add up as expected (one session,
     * max of waves, sum of kills).
     */
    @Test
    void twoPlayerSession() throws Exception {
        StatsStore subject = getStore();

        // Player 1
        UUID id1 = UUID.fromString("deadbeef-ea75-dead-cafe-deadbeef0002");
        String name1 = "bob";

        // Player 2
        UUID id2 = UUID.fromString("deadbeef-ea75-dead-cafe-deadbeef0003");
        String name2 = "carol";

        // Create and save the session
        UUID sessionId = UUID.fromString("cafebabe-ea75-dead-beef-deadbabe0002");
        String arenaSlug = "island";
        Session session = new Session(sessionId, arenaSlug);
        set(session, 610, 23, SessionConclusion.DEFEAT);
        set(session, id1, name1, "tank", -197, -45, null, 310, 3, 6, PlayerConclusion.DEFEAT);
        set(session, id2, name2, "archer", -99, 0, null, 610, 27, 23, PlayerConclusion.DEFEAT);
        subject.save(session);

        try {
            // For global stats, we expect to see:
            // - Total sessions: 1
            // - Total duration: 610 secs
            // - Total kills: 3 + 27 = 30
            // - Total waves: 23
            {
                GlobalStats stats = subject.getGlobalStats();
                test(stats, 1, 610, 30, 23);
            }

            // For arena-specific stats, because we only have a single
            // session, we expect to see the same values for totals,
            // but a real "high score" for the kills:
            // - Highest wave: 23
            // - Longest duration: 610 secs
            // - Highest kills: 27
            {
                ArenaStats stats = subject.getArenaStats(arenaSlug);
                test(stats, 23, 610, 27, 1, 610, 30, 23);
            }

            // For player-specific stats, we expect to see individual numbers:
            // - Total sessions: 1 for both
            // - Total duration: 310 and 610 secs
            // - Total kills: 3 and 27
            // - Total waves: 6 and 23
            {
                PlayerStats stats = subject.getPlayerStats(name1);
                test(stats, 1, 310, 3, 6);
            }
            {
                PlayerStats stats = subject.getPlayerStats(name2);
                test(stats, 1, 610, 27, 23);
            }
        } finally {
            subject.delete(sessionId);
        }
    }

    /**
     * A solid mix of arenas and players.
     * <p>
     * This is "the big one" where multiple players join multiple arenas in
     * various combinations, which means the stats should "stretch" in the
     * extremes to show any inconsistencies.
     */
    @Test
    void multiPlayerMultiSession() throws Exception {
        StatsStore subject = getStore();

        // Player 1
        UUID id1 = UUID.fromString("deadbeef-ea75-dead-cafe-deadbeef0004");
        String name1 = "dennis";

        // Player 2
        UUID id2 = UUID.fromString("deadbeef-ea75-dead-cafe-deadbeef0005");
        String name2 = "eunice";

        // Player 3
        UUID id3 = UUID.fromString("deadbeef-ea75-dead-cafe-deadbeef0006");
        String name3 = "frank";

        // Player 4
        UUID id4 = UUID.fromString("deadbeef-ea75-dead-cafe-deadbeef0007");
        String name4 = "gloria";

        // Arena slugs
        String slug1 = "jungle";
        String slug2 = "caverns";

        // Session IDs
        UUID sessionId1 = UUID.fromString("cafebabe-ea75-dead-beef-deadbabe0003");
        UUID sessionId2 = UUID.fromString("cafebabe-ea75-dead-beef-deadbabe0004");
        UUID sessionId3 = UUID.fromString("cafebabe-ea75-dead-beef-deadbabe0005");

        // Create and save the sessions
        {
            Session session = new Session(sessionId1, slug1);
            set(session, 730, 11, SessionConclusion.DEFEAT);
            set(session, id1, name1, "chemist", -10, 0, 100, null, 1, 3, PlayerConclusion.RETREAT);
            set(session, id2, name2, "oddjob", -50, -5, null, 730, 11, 11, PlayerConclusion.DEFEAT);
            subject.save(session);
        }
        {
            Session session = new Session(sessionId2, slug1);
            set(session, 400, 20, SessionConclusion.VICTORY);
            set(session, id1, name1, "tank", -120, -60, null, 310, 5, 5, PlayerConclusion.DEFEAT);
            set(session, id3, name3, "chemist", -110, -50, null, null, 10, 20, PlayerConclusion.VICTORY);
            subject.save(session);
        }
        {
            Session session = new Session(sessionId3, slug2);
            set(session, 610, 25, SessionConclusion.DEFEAT);
            set(session, id1, name1, "tank", -197, -45, null, 610, 2, 25, PlayerConclusion.DEFEAT);
            set(session, id2, name2, "archer", -99, 0, null, 550, 150, 15, PlayerConclusion.DEFEAT);
            set(session, id3, name3, "knight", -99, -10, null, 500, 10, 10, PlayerConclusion.DEFEAT);
            set(session, id4, name4, "oddjob", -10, -5, 200, null, 1, 3, PlayerConclusion.RETREAT);
            subject.save(session);
        }

        try {
            // Global stats:
            // - Total sessions: 3
            // - Total duration: (730 + 400 + 610) = 1740 secs
            // - Total kills: (11 + 1) + (5 + 10) + (2 + 150 + 10 + 1) = 190
            // - Total waves: (11 + 20 + 25) = 56
            {
                GlobalStats stats = subject.getGlobalStats();
                test(stats, 3, 1740, 190, 56);
            }

            // First arena stats:
            // - Highest wave: 20 (second session)
            // - Longest duration: 730 secs (first session)
            // - Highest kills: 11 (first session)
            // - Total sessions: 2
            // - Total duration: 730 + 400 = 1130 secs
            // - Total kills: (11 + 1) + (5 + 10) = 27
            // - Total waves: 11 + 20 = 31
            {
                ArenaStats stats = subject.getArenaStats(slug1);
                test(stats, 20, 730, 11, 2, 1130, 27, 31);
            }

            // Second arena stats:
            // - Highest wave: 25
            // - Longest duration: 610
            // - Highest kills: 150
            // - Total sessions: 1
            // - Total duration: 610
            // - Total kills: (2 + 150 + 10 + 1) = 163
            // - Total waves: 25
            {
                ArenaStats stats = subject.getArenaStats(slug2);
                test(stats, 25, 610, 150, 1, 610, 163, 25);
            }

            // Player 1 stats:
            // - Total sessions: 3
            // - Total duration: (100 + 310 + 610) = 1020
            // - Total kills: (1 + 5 + 2) = 8
            // - Total waves: (3 + 5 + 25) = 33
            {
                PlayerStats stats = subject.getPlayerStats(name1);
                test(stats, 3, 1020, 8, 33);
            }

            // Player 2 stats:
            // - Total sessions: 2
            // - Total duration: (730 + 550) = 1280
            // - Total kills: (11 + 150) = 161
            // - Total waves: (11 + 15) = 26
            {
                PlayerStats stats = subject.getPlayerStats(name2);
                test(stats, 2, 1280, 161, 26);
            }

            // Player 3 stats:
            // - Total sessions: 2
            // - Total duration: (400 + 500) = 900
            // - Total kills: (10 + 10) = 20
            // - Total waves: (20 + 10) = 30
            {
                PlayerStats stats = subject.getPlayerStats(name3);
                test(stats, 2, 900, 20, 30);
            }

            // Player 4 stats:
            // - Total sessions: 1
            // - Total duration: 200
            // - Total kills: 1
            // - Total waves: 3
            {
                PlayerStats stats = subject.getPlayerStats(name4);
                test(stats, 1, 200, 1, 3);
            }
        } finally {
            subject.delete(sessionId1);
            subject.delete(sessionId2);
            subject.delete(sessionId3);
        }
    }

    static final Instant epoch = Instant.parse("2021-06-28T10:00:00Z");

    private static void set(
        Session session,
        int endOffset,
        int lastWave,
        SessionConclusion conclusion
    ) {
        SessionStats stats = session.getSessionStats();
        stats.startTime = epoch;
        stats.endTime = epoch.plusSeconds(endOffset);
        stats.lastWave = lastWave;
        stats.conclusion = conclusion;
    }

    private static void set(
        Session session,
        UUID playerId,
        String playerName,
        String className,
        int joinOffset,
        int readyOffset,
        Integer leaveOffset,
        Integer deathOffset,
        int kills,
        int lastWave,
        PlayerConclusion conclusion
    ) {
        PlayerSessionStats stats = new PlayerSessionStats(session.getSessionId(), playerId, playerName);
        stats.className = className;
        stats.joinTime = epoch.plusSeconds(joinOffset);
        stats.readyTime = epoch.plusSeconds(readyOffset);
        stats.leaveTime = (leaveOffset != null) ? epoch.plusSeconds(leaveOffset) : null;
        stats.deathTime = (deathOffset != null) ? epoch.plusSeconds(deathOffset) : null;
        stats.kills = kills;
        stats.lastWave = lastWave;
        stats.conclusion = conclusion;
        session.setPlayerStats(stats.playerId, stats);
    }

    private static void test(
        GlobalStats stats,
        int totalSessions,
        long totalSeconds,
        long totalKills,
        long totalWaves
    ) {
        assertThat(stats.totalSessions, equalTo(totalSessions));
        assertThat(stats.totalSeconds, equalTo(totalSeconds));
        assertThat(stats.totalKills, equalTo(totalKills));
        assertThat(stats.totalWaves, equalTo(totalWaves));
    }

    private static void test(
        ArenaStats stats,
        int highestWave,
        int highestSeconds,
        int highestKills,
        int totalSessions,
        long totalSeconds,
        long totalKills,
        long totalWaves
    ) {
        assertThat(stats.highestWave, equalTo(highestWave));
        assertThat(stats.highestSeconds, equalTo(highestSeconds));
        assertThat(stats.highestKills, equalTo(highestKills));
        assertThat(stats.totalSessions, equalTo(totalSessions));
        assertThat(stats.totalSeconds, equalTo(totalSeconds));
        assertThat(stats.totalKills, equalTo(totalKills));
        assertThat(stats.totalWaves, equalTo(totalWaves));
    }

    private static void test(
        PlayerStats stats,
        int totalSessions,
        long totalSeconds,
        long totalKills,
        long totalWaves
    ) {
        assertThat(stats.totalSessions, equalTo(totalSessions));
        assertThat(stats.totalSeconds, equalTo(totalSeconds));
        assertThat(stats.totalKills, equalTo(totalKills));
        assertThat(stats.totalWaves, equalTo(totalWaves));
    }


}
