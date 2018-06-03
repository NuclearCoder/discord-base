package nuke.discord.command.meta.selectors

import nuke.discord.command.meta.registry.RegisteredCommand
import org.apache.commons.collections4.trie.PatriciaTrie

object PrefixSelector : CommandSelector {

    private val map = PatriciaTrie<RegisteredCommand>()

    override fun add(command: RegisteredCommand) {
        map[command.name] = command
    }

    override fun select(name: String): RegisteredCommand? {
        return map.prefixMap(name).let { it.getOrDefault(it.firstKey(), null) }
    }

    override fun toMap(): Map<String, RegisteredCommand> {
        return map.toMap()
    }

}