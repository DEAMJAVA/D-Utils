package net.deamjava.d_utils

import net.deamjava.d_utils.config.DUtilsConfig
import net.deamjava.d_utils.gui.DUtilsConfigScreen
import net.deamjava.d_utils.mixin.client.KeyMappingAccessor
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.KeyMapping.Category
import net.minecraft.client.gui.components.toasts.SystemToast
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
object DUtilsClient : ClientModInitializer {

    val LOGGER = LoggerFactory.getLogger("d_utils")

    private lateinit var openConfigKey: KeyMapping
    private lateinit var toggleProtectionKey: KeyMapping

    private var openConfigDownLast = false
    private var toggleProtectionDownLast = false

    private val CATEGORY: Category = Category.register(Identifier.fromNamespaceAndPath("d_utils", "main"))

    override fun onInitializeClient() {
        LOGGER.info("D Utils initializing — privacy protection starting up")

        DUtilsConfig.load()

        openConfigKey = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.d_utils.open_config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                CATEGORY
            )
        )

        toggleProtectionKey = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.d_utils.toggle_protection",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->

            val window = client.window

            val openPressedNow = InputConstants.isKeyDown(
                window,
                (openConfigKey as KeyMappingAccessor).key.value
            )

            val togglePressedNow = InputConstants.isKeyDown(
                window,
                (toggleProtectionKey as KeyMappingAccessor).key.value  // ✅ FIXED
            )

            if (openPressedNow && !openConfigDownLast) {
                client.setScreen(DUtilsConfigScreen(client.screen))
            }

            if (togglePressedNow && !toggleProtectionDownLast) {
                val cfg = DUtilsConfig.instance
                cfg.protectionEnabled = !cfg.protectionEnabled
                cfg.save()

                val toastKey =
                    if (cfg.protectionEnabled) "d_utils.toast.enabled"
                    else "d_utils.toast.disabled"

                client.toastManager.addToast(
                    SystemToast.multiline(
                        client,
                        SystemToast.SystemToastId.NARRATOR_TOGGLE,
                        Component.literal("D Utils"),
                        Component.translatable(toastKey)
                    )
                )

                LOGGER.info("D Utils protection toggled: ${cfg.protectionEnabled}")
            }

            openConfigDownLast = openPressedNow
            toggleProtectionDownLast = togglePressedNow
        }

        LOGGER.info("D Utils ready. Protection active: ${DUtilsConfig.instance.protectionEnabled}")
    }
}