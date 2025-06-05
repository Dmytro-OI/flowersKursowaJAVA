package logging;

import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class LoggerConfigTest {

    @Test
    public void testGetLogger_ReturnsSameLoggerInstance() {
        Logger logger1 = LoggerConfig.getLogger();
        Logger logger2 = LoggerConfig.getLogger();

        assertNotNull(logger1);
        assertSame(logger1, logger2);
        assertTrue(logger1.getLevel().intValue() <= Level.ALL.intValue());
    }

    @Test
    public void testSendCriticalErrorEmail_DoesNotThrow() {
        assertDoesNotThrow(() -> LoggerConfig.sendCriticalErrorEmail("Тест критичної помилки"));
    }
}