package net.deamjava.d_utils.gui

import net.deamjava.d_utils.config.DUtilsConfig
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.network.chat.Component

/**
 * A clean, user-friendly config screen for D Utils.
 *
 * Accessible via the keybind (default: K) or from mod menus like Mod Menu.
 */
class DUtilsConfigScreen(private val parent: Screen?) : Screen(Component.translatable("d_utils.config.title")) {

    private val config get() = DUtilsConfig.instance

    companion object {
        private const val BUTTON_WIDTH = 200
        private const val BUTTON_HEIGHT = 20
        private const val BUTTON_SPACING = 24
    }

    override fun init() {
        val centerX = width / 2 - BUTTON_WIDTH / 2
        var y = height / 2 - (BUTTON_HEIGHT + BUTTON_SPACING) * 2

        // Master toggle
        addRenderableWidget(
            CycleButton.onOffBuilder(config.protectionEnabled)
                .withTooltip { value ->
                    net.minecraft.client.gui.components.Tooltip.create(
                        Component.translatable("d_utils.config.protection_enabled.tooltip")
                    )
                }
                .create(
                    centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Component.translatable("d_utils.config.protection_enabled")
                ) { _, value ->
                    config.protectionEnabled = value
                    config.save()
                    // Refresh buttons to show enabled state
                    rebuildWidgets()
                }
        )
        y += BUTTON_SPACING

        // Sign protection
        addRenderableWidget(
            CycleButton.onOffBuilder(config.signProtection)
                .withTooltip { _ ->
                    net.minecraft.client.gui.components.Tooltip.create(
                        Component.translatable("d_utils.config.sign_protection.tooltip")
                    )
                }
                .create(
                    centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Component.translatable("d_utils.config.sign_protection")
                ) { _, value ->
                    config.signProtection = value
                    config.save()
                }
        )
        y += BUTTON_SPACING

        // Anvil protection
        addRenderableWidget(
            CycleButton.onOffBuilder(config.anvilProtection)
                .withTooltip { _ ->
                    net.minecraft.client.gui.components.Tooltip.create(
                        Component.translatable("d_utils.config.anvil_protection.tooltip")
                    )
                }
                .create(
                    centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Component.translatable("d_utils.config.anvil_protection")
                ) { _, value ->
                    config.anvilProtection = value
                    config.save()
                }
        )
        y += BUTTON_SPACING

        // Book protection
        addRenderableWidget(
            CycleButton.onOffBuilder(config.bookProtection)
                .withTooltip { _ ->
                    net.minecraft.client.gui.components.Tooltip.create(
                        Component.translatable("d_utils.config.book_protection.tooltip")
                    )
                }
                .create(
                    centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Component.translatable("d_utils.config.book_protection")
                ) { _, value ->
                    config.bookProtection = value
                    config.save()
                }
        )
        y += BUTTON_SPACING + 8

        // Close / Done button
        addRenderableWidget(
            Button.builder(Component.literal("Done")) { onClose() }
                .bounds(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build()
        )
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
        // Title
        graphics.centeredText(
            font,
            title,
            width / 2,
            height / 2 - (BUTTON_HEIGHT + BUTTON_SPACING) * 2 - 20,
            0xFFFFFF
        )
        // Status indicator
        val statusKey = if (config.protectionEnabled) "d_utils.status.enabled" else "d_utils.status.disabled"
        val statusColor = if (config.protectionEnabled) 0x55FF55 else 0xFF5555
        graphics.centeredText(
            font,
            Component.translatable(statusKey),
            width / 2,
            height / 2 - (BUTTON_HEIGHT + BUTTON_SPACING) * 2 - 8,
            statusColor
        )
    }

    override fun isPauseScreen(): Boolean = false

    override fun onClose() {
        minecraft?.setScreen(parent)
    }
}
