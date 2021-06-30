package org.mobarena.stats.session;

public enum SessionConclusion {

    /**
     * When an arena has a final wave and one or more players reach and
     * complete that wave, the session concludes in a victory.
     */
    VICTORY,

    /**
     * When the last player alive in an arena dies, the session concludes
     * in a defeat, meaning the players "lost" the session.
     */
    DEFEAT,

}
