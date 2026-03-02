package net.deamjava.d_utils.mixin.client;

import net.deamjava.d_utils.config.DUtilsConfig;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

/**
 * Intercepts translation key resolution at the concrete implementation level.
 *
 * We target TranslationStorage.get(String key, String fallback) rather than the
 * abstract Language.get() because abstract methods have no bytecode body — there
 * is no @AT to resolve inside them. TranslationStorage is the sole concrete
 * subclass and is where all actual lookups happen.
 *
 * For any key whose namespace is not vanilla, we return the raw key unchanged.
 * This makes every client look identical from the server's perspective: mod keys
 * always echo back as themselves, regardless of whether the mod is installed.
 */
@Mixin(TranslationStorage.class)
public abstract class LanguageMixin {

//    private static final Set<String> VANILLA_NAMESPACES = Set.of(
//            "block", "item", "entity", "biome", "effect", "enchantment",
//            "attribute", "death", "commands", "argument", "advancement",
//            "chat", "key", "controls", "options", "menu", "selectWorld",
//            "createWorld", "pack", "gui", "gameMode", "difficulty",
//            "narrator", "accessibility", "structure", "team", "demo",
//            "language", "resourcePack", "disconnect", "multiplayer",
//            "connect", "spectatorMenu", "painting", "stat", "book",
//            "container", "inventory", "merchant", "filled_map",
//            "swimming", "fall", "subtitles", "record", "note_block",
//            "potion", "trim_material", "trim_pattern", "upgrade",
//            "jukebox_song", "advancements", "gamerule", "lantern_hang",
//            "hanging", "keybind", "text", "sound", "particle",
//            "generator", "flat", "preset"
//    );

    private static final Set<String> VANILLA_NAMESPACES = Set.of();

    @Inject(method = "get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void dutils_blockModTranslation(String key, String fallback, CallbackInfoReturnable<String> cir) {
        if (!DUtilsConfig.Companion.getInstance().getProtectionEnabled()) return;

        // Only intercept keys that look like translation keys (contain a dot)
        int firstDot = key.indexOf('.');
        if (firstDot <= 0) return;

        String namespace = key.substring(0, firstDot);

        // Non-vanilla namespace: return the raw key so the server cannot detect mods
        if (!VANILLA_NAMESPACES.contains(namespace)) {
            cir.setReturnValue(key);
        }
    }
}