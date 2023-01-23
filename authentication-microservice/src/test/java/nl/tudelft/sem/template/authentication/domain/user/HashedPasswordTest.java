package nl.tudelft.sem.template.authentication.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HashedPasswordTest {

    private String hash;
    private HashedPassword hp1;
    private HashedPassword hp2;
    private HashedPassword hp3;

    @BeforeEach
    void setup() {
        this.hash = "test12%";
        String hash2 = "test34%";
        this.hp1 = new HashedPassword(hash);
        this.hp2 = new HashedPassword(hash2);
        this.hp3 = new HashedPassword(hash);
    }

    @Test
    void testToString() {
        assertThat(hp1.toString()).isEqualTo(hash);
    }

    @Test
    void testEquals() {
        assertThat(hp1.equals(hp2)).isTrue();
        assertThat(hp1.equals(hp1)).isTrue();
        assertThat(hp1.equals(hp3)).isTrue();
    }

}