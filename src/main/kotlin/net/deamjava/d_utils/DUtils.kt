package net.deamjava.d_utils

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

/**
 * D Utils common (server-safe) entrypoint.
 * All actual logic is client-side — this is here for structural completeness.
 */
object DUtils : ModInitializer {
    val LOGGER = LoggerFactory.getLogger("d_utils")

    override fun onInitialize() {
        // Nothing to do on the common side — mod is client-only
    }
}
