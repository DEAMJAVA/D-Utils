package net.deamjava.d_utils.mixin.client;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

/**
 * Accessor for TextFieldWidget's private changedListener field.
 * This lets AnvilScreenMixin read the current listener before wrapping it,
 * so the original vanilla packet-sending listener is preserved.
 */
@Mixin(EditBox.class)
public interface EditBoxAccessor {

    @Accessor("responder")
    Consumer<String> dutils_getResponder();
}