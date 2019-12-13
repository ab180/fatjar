package co.ab180.fatjar

import org.gradle.api.logging.Logger

import java.lang.ref.WeakReference

class LoggingUtils {

    private static final String TAG = "[FAT JAR]"

    private static WeakReference<Logger> loggerRef

    static void init(Logger logger) {
        loggerRef = new WeakReference<>(logger)
    }

    static void println(String message) {
        Logger logger = loggerRef.get()
        if (logger == null) {
            return
        }

        logger.println("$TAG $message")
    }

    static void info(String message, Object ... objects) {
        Logger logger = loggerRef.get()
        if (logger == null) {
            return
        }

        logger.info("$TAG $message", objects)
    }
}
