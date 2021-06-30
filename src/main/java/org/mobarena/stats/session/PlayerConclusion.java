package org.mobarena.stats.session;

public enum PlayerConclusion {

    /**
     * When an arena has a final wave and the given player reaches and
     * completes that wave, the player session concludes in a victory.
     */
    VICTORY,

    /**
     * When the given player dies in an arena, the player's session will
     * concludes in a defeat, even if other players are still alive.
     */
    DEFEAT,

    /**
     * When the given player leaves an ongoing arena, the player's session
     * will conclude in a retreat, even if other players are still alive.
     */
    RETREAT,

}
