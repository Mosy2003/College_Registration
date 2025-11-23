package GUIS;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class Step3_InMemoryStoreSession_StoreTest {

    @Test
    void store_accepts_unique_and_rejects_duplicate_ids() {
        Step3_InMemoryStoreSession.Store store = new Step3_InMemoryStoreSession.Store();

        Step3_InMemoryStoreSession.Student s1 = new Step3_InMemoryStoreSession.Student(
                "S01", "Alice", "a@x.com", "12345", UUID.randomUUID());
        store.save(s1); // should pass

        // same ID should now fail
        Step3_InMemoryStoreSession.Student dup = new Step3_InMemoryStoreSession.Student(
                "S01", "Bob", "b@x.com", "999", UUID.randomUUID());

        assertThrows(IllegalArgumentException.class, () -> store.save(dup),
                "Expected duplicate Student ID to throw exception");
        assertFalse(store.isUnique("S01"), "S01 should no longer be unique");
    }
}
