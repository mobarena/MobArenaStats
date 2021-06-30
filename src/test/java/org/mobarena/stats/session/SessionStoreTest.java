package org.mobarena.stats.session;

import com.garbagemule.MobArena.framework.Arena;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class SessionStoreTest {

    SessionStore subject;

    @BeforeEach
    void setup() {
        subject = SessionStore.createNew();
    }

    @Test
    void createThrowsIfSessionAlreadyExists() {
        Arena arena = Mocks.arena("jungle");
        subject.create(arena);

        assertThrows(
            IllegalStateException.class,
            () -> subject.create(arena)
        );
    }

    @Test
    void getByArenaOnFreshStoreReturnsNull() {
        Arena arena = Mocks.arena("castle");

        Session result = subject.getByArena(arena);

        assertThat(result, is(nullValue()));
    }

    @Test
    void getByArenaAfterCreateReturnsSameSession() {
        Arena arena = Mocks.arena("castle");

        Session expected = subject.create(arena);
        Session result = subject.getByArena(arena);

        assertThat(result, equalTo(expected));
    }

    @Test
    void getByArenaAfterDeleteReturnsNull() {
        Arena arena = Mocks.arena("castle");

        Session session = subject.create(arena);
        subject.delete(session);
        Session result = subject.getByArena(arena);

        assertThat(result, is(nullValue()));
    }

}
