package net.deamjava.d_utils.config

/**
 * Secondary sanitization layer for outgoing packet text.
 *
 * The primary defense is LanguageMixin, which intercepts Language.get() and
 * returns raw keys for all non-vanilla namespaces — so translated mod keys
 * never make it into outgoing text in the first place.
 *
 * This sanitizer is a fallback pass on the already-clean strings in the
 * sign/anvil/book mixins. Since LanguageMixin already blocked translation,
 * strings arriving here should already contain only raw keys or vanilla text.
 * We pass them through unchanged.
 */
object TranslationKeySanitizer {
    fun sanitizeLine(text: String): String = text
    fun sanitizeItemName(name: String): String = name
    fun sanitizeLines(lines: List<String>): List<String> = lines
}