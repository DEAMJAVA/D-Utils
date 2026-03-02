package net.deamjava.d_utils.gui

import net.deamjava.d_utils.config.DUtilsConfig
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.CyclingButtonWidget
import net.minecraft.text.Text

/**
 * A clean, user-friendly config screen for D Utils.
 *
 * Accessible via the keybind (default: K) or from mod menus like Mod Menu.
 */
class DUtilsConfigScreen(private val parent: Screen?) : Screen(Text.translatable("d_utils.config.title")) {

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
        addDrawableChild(
            CyclingButtonWidget.onOffBuilder(config.protectionEnabled)
                .tooltip { value ->
                    net.minecraft.client.gui.tooltip.Tooltip.of(
                        Text.translatable("d_utils.config.protection_enabled.tooltip")
                    )
                }
                .build(
                    centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Text.translatable("d_utils.config.protection_enabled")
                ) { _, value ->
                    config.protectionEnabled = value
                    config.save()
                    // Refresh buttons to show enabled state
                    clearAndInit()
                }
        )
        y += BUTTON_SPACING

        // Sign protection
        addDrawableChild(
            CyclingButtonWidget.onOffBuilder(config.signProtection)
                .tooltip { _ ->
                    net.minecraft.client.gui.tooltip.Tooltip.of(
                        Text.translatable("d_utils.config.sign_protection.tooltip")
                    )
                }
                .build(
                    centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Text.translatable("d_utils.config.sign_protection")
                ) { _, value ->
                    config.signProtection = value
                    config.save()
                }
        )
        y += BUTTON_SPACING

        // Anvil protection
        addDrawableChild(
            CyclingButtonWidget.onOffBuilder(config.anvilProtection)
                .tooltip { _ ->
                    net.minecraft.client.gui.tooltip.Tooltip.of(
                        Text.translatable("d_utils.config.anvil_protection.tooltip")
                    )
                }
                .build(
                    centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Text.translatable("d_utils.config.anvil_protection")
                ) { _, value ->
                    config.anvilProtection = value
                    config.save()
                }
        )
        y += BUTTON_SPACING

        // Book protection
        addDrawableChild(
            CyclingButtonWidget.onOffBuilder(config.bookProtection)
                .tooltip { _ ->
                    net.minecraft.client.gui.tooltip.Tooltip.of(
                        Text.translatable("d_utils.config.book_protection.tooltip")
                    )
                }
                .build(
                    centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Text.translatable("d_utils.config.book_protection")
                ) { _, value ->
                    config.bookProtection = value
                    config.save()
                }
        )
        y += BUTTON_SPACING + 8

        // Close / Done button
        addDrawableChild(
            ButtonWidget.builder(Text.literal("Done")) { close() }
                .dimensions(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build()
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        // Title
        context.drawCenteredTextWithShadow(
            textRenderer,
            title,
            width / 2,
            height / 2 - (BUTTON_HEIGHT + BUTTON_SPACING) * 2 - 20,
            0xFFFFFF
        )
        // Status indicator
        val statusKey = if (config.protectionEnabled) "d_utils.status.enabled" else "d_utils.status.disabled"
        val statusColor = if (config.protectionEnabled) 0x55FF55 else 0xFF5555
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.translatable(statusKey),
            width / 2,
            height / 2 - (BUTTON_HEIGHT + BUTTON_SPACING) * 2 - 8,
            statusColor
        )
    }

    override fun shouldPause(): Boolean = false

    override fun close() {
        client?.setScreen(parent)
    }
}
