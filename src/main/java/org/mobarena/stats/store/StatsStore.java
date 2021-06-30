package org.mobarena.stats.store;

import org.mobarena.stats.session.Session;

import java.io.IOException;
import java.util.UUID;

/**
 * Persistent data store for session and player stats.
 * <p>
 * All store operations are <i>blocking</i>, meaning any calls to the store
 * should be handled off the main thread to prevent performance impacts.
 */
public interface StatsStore {

    /**
     * Store the given {@link Session}'s data in the store.
     * <p>
     * This method should only be called with a "finished" session, i.e. a
     * session that has concluded and won't be altered after saving.
     *
     * @param session a session to store
     * @throws IOException if the operation fails due to I/O
     */
    void save(Session session) throws IOException;

    /**
     * Delete all data about the session with the given ID.
     * <p>
     * Removes all session and player data associated with the session of
     * the given ID, meaning these stats will be lost forever.
     *
     * @param sessionId the ID of the session whose data to delete
     */
    void delete(UUID sessionId);

    // TODO: docs
    GlobalStats getGlobalStats();

    // TODO: docs
    ArenaStats getArenaStats(String slug);

    // TODO: docs
    PlayerStats getPlayerStats(String name);

    // TODO: docs
    void export(StatsStore target) throws IOException;

}
