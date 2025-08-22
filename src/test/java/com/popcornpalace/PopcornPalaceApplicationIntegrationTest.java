package com.popcornpalace;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PopcornPalaceApplicationIntegrationTest {

    @Test
    void contextLoads() {
        // This test will fail if the application context cannot start
        // It verifies that all beans can be created and wired together
    }
}
