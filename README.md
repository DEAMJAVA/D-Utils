# D Utils

**Fabric · Minecraft 1.21.11 · Client-side**

D Utils protects your privacy by preventing servers from detecting which mods you have installed via the translation key exploit.

---

## The Exploit

Minecraft allows text in signs, anvil rename fields, and books to be specified as a **translation key** — for example `sodium.option_impact.low`. The client resolves this key to its translated value (`Low`) before sending the text back to the server. A malicious server can abuse this:

1. It opens a sign or anvil on your client with a mod-specific translation key as the text.
2. If you have that mod installed, your client resolves the key and sends back the translated value.
3. If you don't have it, the raw key is echoed back unchanged.
4. The server now knows whether you have that mod installed.

This works for **any mod that ships translation files** — which is nearly every mod.

---

## How D Utils Fixes It

Rather than scanning outgoing packets (which is fragile and produces false positives), D Utils intercepts translation resolution at the source.

It mixins into `TranslationStorage.get(String key, String fallback)` — the single method every translation lookup in the game flows through. For any key whose namespace does not belong to vanilla Minecraft, it returns the **raw key unchanged** instead of the translated value.

This means the server always receives the raw key regardless of whether the mod is installed, making every client look identical.

Vanilla UI is completely unaffected. Only non-vanilla namespaces are blocked.

---

## Keybinds

| Keybind | Default | Description |
|---|---|---|
| Open Config Screen | `K` | Opens the D Utils settings screen |
| Toggle Protection | Unbound | Instantly enables/disables protection from anywhere, including inside GUIs |

Both keybinds can be rebound in **Options → Controls → D Utils**.

The toggle keybind works globally — in inventories, pause menus, chat, any screen — so you can quickly disable protection if you need to read mod UI text, then re-enable it.

---

## Config

Located at `.minecraft/config/d_utils.json`:

```json
{
  "protectionEnabled": true,
  "signProtection": true,
  "anvilProtection": true,
  "bookProtection": true
}
```

`protectionEnabled` is the master switch. The individual toggles only take effect when the master switch is on.

---

---

## Dependencies

| Dependency | Version |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | 0.18.1+ |
| Fabric API | 0.139.5+1.21.11 |
| Fabric Language Kotlin | 1.13.4+kotlin.2.2.0 |

---

## License

[MIT](LICENSE)