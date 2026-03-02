package net.deamjava.d_utils

import net.deamjava.d_utils.config.DUtilsConfig
import net.deamjava.d_utils.gui.DUtilsConfigScreen
import net.deamjava.d_utils.mixin.client.KeyBindingAccessor
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.KeyBinding.Category
import net.minecraft.client.toast.SystemToast
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
object DUtilsClient : ClientModInitializer {

    val LOGGER = LoggerFactory.getLogger("d_utils")

    private lateinit var openConfigKey: KeyBinding
    private lateinit var toggleProtectionKey: KeyBinding

    private var openConfigDownLast = false
    private var toggleProtectionDownLast = false

    private val CATEGORY: Category = Category.create(Identifier.of("d_utils", "main"))

    override fun onInitializeClient() {
        LOGGER.info("D Utils initializing — privacy protection starting up")

        DUtilsConfig.load()

        openConfigKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.d_utils.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                CATEGORY
            )
        )

        toggleProtectionKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.d_utils.toggle_protection",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->

            val window = client.window  // ✅ FIXED

            val openPressedNow = InputUtil.isKeyPressed(
                window,
                (openConfigKey as KeyBindingAccessor).boundKey.code
            )

            val togglePressedNow = InputUtil.isKeyPressed(
                window,
                (toggleProtectionKey as KeyBindingAccessor).boundKey.code  // ✅ FIXED
            )

            if (openPressedNow && !openConfigDownLast) {
                client.setScreen(DUtilsConfigScreen(client.currentScreen))
            }

            if (togglePressedNow && !toggleProtectionDownLast) {
                val cfg = DUtilsConfig.instance
                cfg.protectionEnabled = !cfg.protectionEnabled
                cfg.save()

                val toastKey =
                    if (cfg.protectionEnabled) "d_utils.toast.enabled"
                    else "d_utils.toast.disabled"

                client.toastManager.add(
                    SystemToast.create(
                        client,
                        SystemToast.Type.NARRATOR_TOGGLE,
                        Text.literal("D Utils"),
                        Text.translatable(toastKey)
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