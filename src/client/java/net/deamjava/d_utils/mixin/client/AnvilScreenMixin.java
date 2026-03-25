package net.deamjava.d_utils.mixin.client;

import net.deamjava.d_utils.config.DUtilsConfig;
import net.deamjava.d_utils.config.TranslationKeySanitizer;
import net.deamjava.d_utils.mixin.client.EditBoxAccessor;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * Intercepts the anvil rename flow.
 *
 * The exploit: the server places an item whose display-name is a mod-specific translation
 * key into the anvil's input slot. The client resolves the key and populates the rename
 * text-field with the translated value. Every change to the field fires a
 * RenameItemC2SPacket with that resolved value.
 *
 * We wrap the text-field's change listener so the text is sanitized before the packet fires.
 * We use TextFieldWidgetAccessor to read the original listener before wrapping it.
 */
@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {

    @Shadow
    private EditBox name;

    @Inject(method = "subInit", at = @At("RETURN"))
    private void dutils_hookRenameField(CallbackInfo ci) {
        if (name == null) return;
        if (!DUtilsConfig.Companion.getInstance().isAnvilProtectionActive()) return;

        // Read the original listener installed by vanilla's setup() via the accessor
        Consumer<String> original = ((EditBoxAccessor) name).dutils_getResponder();

        name.setResponder(text -> {
            String sanitized = TranslationKeySanitizer.INSTANCE.sanitizeItemName(text);
            if (!sanitized.equals(text)) {
                // Text was a resolved mod translation key — replace with sanitized value.
                // Detach listener temporarily to avoid a recursive callback loop.
                name.setResponder(null);
                name.setValue(sanitized);
                name.setResponder(this::dutils_noop); // placeholder to avoid NPE
                // Now fire the original listener with the clean text
                if (original != null) original.accept(sanitized);
                // Restore our wrapper
                name.setResponder(text2 -> {
                    String s2 = TranslationKeySanitizer.INSTANCE.sanitizeItemName(text2);
                    if (original != null) original.accept(s2);
                });
            } else {
                if (original != null) original.accept(text);
            }
        });
    }

    private void dutils_noop(String s) {}
}