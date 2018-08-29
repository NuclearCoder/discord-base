package nuke.discord.command.meta.selectors

import nuke.discord.command.meta.registry.RegisteredCommand
import nuke.discord.util.toLowerCase
import org.apache.commons.collections4.trie.PatriciaTrie

private class GenericPrefixSelector(val caseSensitive: Boolean) : CommandSelector {

    private val map = PatriciaTrie<RegisteredCommand>()

    override fun add(command: RegisteredCommand) {
        map[command.name.toLowerCase(caseSensitive)] = command
    }

    override fun select(name: String): RegisteredCommand? {
        return map.prefixMap(name.toLowerCase(caseSensitive))
                .takeIf { it.isNotEmpty() }
                ?.let { it.getOrDefault(it.firstKey(), null) }
    }

    override fun toMap(): Map<String, RegisteredCommand> {
        return map.toMap()
    }

}

object PrefixSelector : CommandSelector by GenericPrefixSelector(true) {
    object CaseInsensitive : CommandSelector by GenericPrefixSelector(false)
}