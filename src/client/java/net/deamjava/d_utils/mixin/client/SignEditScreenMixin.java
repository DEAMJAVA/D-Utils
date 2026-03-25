package net.deamjava.d_utils.mixin.client;

import net.deamjava.d_utils.config.DUtilsConfig;
import net.deamjava.d_utils.config.TranslationKeySanitizer;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts the sign finalization packet.
 *
 * AbstractSignEditScreen.removed() is called when the screen closes.
 * It calls finishEditing() internally, which sends the UpdateSignC2SPacket.
 * The `messages` String[] is populated by the client from the sign text
 * (resolving translation keys) before removed() is triggered.
 *
 * We sanitize the messages array at the head of removed() before finishEditing()
 * runs, so no mod-fingerprint translation values reach the server.
 */
@Mixin(AbstractSignEditScreen.class)
public abstract class SignEditScreenMixin {

    // messages is a private final String[] in AbstractSignEditScreen
    @Shadow
    private String[] messages;

    @Inject(method = "removed", at = @At("HEAD"))
    private void dutils_sanitizeSignLines(CallbackInfo ci) {
        if (!DUtilsConfig.Companion.getInstance().isSignProtectionActive()) return;
        if (messages == null) return;
        for (int i = 0; i < messages.length; i++) {
            messages[i] = TranslationKeySanitizer.INSTANCE.sanitizeLine(messages[i]);
        }
    }
}