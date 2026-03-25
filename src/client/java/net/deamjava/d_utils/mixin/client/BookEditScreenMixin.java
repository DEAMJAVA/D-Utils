package net.deamjava.d_utils.mixin.client;

import net.deamjava.d_utils.config.DUtilsConfig;
import net.deamjava.d_utils.config.TranslationKeySanitizer;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Intercepts book page content before BookUpdateC2SPacket is constructed.
 *
 * BookEditScreen in 1.21.7+ uses an EditBoxWidget instead of List<String> pages,
 * so we cannot shadow that field or target removed(). Instead we use @ModifyArg
 * targeting the INVOKESPECIAL of BookUpdateC2SPacket.<init> wherever it is called
 * inside BookEditScreen. Constructor calls compile to INVOKESPECIAL, which is a
 * valid INVOKE target for Mixin.
 *
 * BookUpdateC2SPacket(int slot, List<String> pages, Optional<String> title)
 * We modify argument index 1 (the pages list) at all call sites in this class.
 */
@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin {

    // TODO(Ravel): wildcard and regex target are not supported
// TODO(Ravel): wildcard and regex target are not supported
    @ModifyArg(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ServerboundEditBookPacket;<init>(ILjava/util/List;Ljava/util/Optional;)V"
            ),
            index = 1
    )
    private List<String> dutils_sanitizeBookPages(List<String> pages) {
        if (!DUtilsConfig.Companion.getInstance().isBookProtectionActive()) return pages;
        return pages.stream()
                .map(page -> TranslationKeySanitizer.INSTANCE.sanitizeLine(page))
                .collect(Collectors.toList());
    }
}