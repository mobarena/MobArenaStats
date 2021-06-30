package org.mobarena.stats.session;

import com.garbagemule.MobArena.framework.Arena;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory store for on-going session data.
 * <p>
 * The session store is the top-level bookkeeping entity for current sessions
 * in that all {@link Session} objects are created and kept track of by this
 * store. Unlike {@link org.mobarena.stats.store.StatsStore}, which persists
 * its data, the session store is just an in-memory collection.
 */
public class SessionStore {

    private final Map<String, Session> slugToSession;

    private SessionStore() {
        this.slugToSession = new HashMap<>();
    }

    /**
     * Create a new {@link Session} instance for the given {@link Arena}.
     * <p>
     * Note that only one session can be active per arena. The method throws
     * if it is called with an arena instance that already has an associated
     * on-going session.
     *
     * @param arena the arena to create a new session for
     * @return a new session for the given arena
     * @throws IllegalStateException if a session exists for the given arena
     */
    public Session create(Arena arena) {
        String arenaSlug = arena.getSlug();

        if (slugToSession.containsKey(arenaSlug)) {
            throw new IllegalStateException("A session for arena " + arenaSlug + " already exists");
        }

        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId, arenaSlug);
        slugToSession.put(arenaSlug, session);

        return session;
    }

    /**
     * Delete the given {@link Session} from the store.
     * <p>
     * When a session is deleted, it opens up the possibility of starting a
     * new one for the associated arena.
     *
     * @param session a session to delete
     */
    public void delete(Session session) {
        slugToSession.remove(session.getArenaSlug());
    }

    /**
     * Look up a {@link Session} by its associated {@link Arena}.
     *
     * @param arena the arena whose session to look up
     * @return the associated session instance, or null
     */
    public Session getByArena(Arena arena) {
        return slugToSession.get(arena.getSlug());
    }

    /**
     * Clear the internal session map.
     * <p>
     * This method is called by MobArenaStats on reloads to try to clear any
     * residue from old sessions.
     */
    public void clear() {
        slugToSession.clear();
    }

    public static SessionStore createNew() {
        return new SessionStore();
    }

}
