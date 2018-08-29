package nuke.discord.command.meta.selectors

import nuke.discord.command.meta.registry.RegisteredCommand
import nuke.discord.util.toLowerCase

private class GenericExactSelector(val caseSensitive: Boolean) : CommandSelector {

    private val map = mutableMapOf<String, RegisteredCommand>()

    override fun add(command: RegisteredCommand) {
        map[command.name.toLowerCase(caseSensitive)] = command
    }

    override fun select(name: String): RegisteredCommand? {
        return map[name.toLowerCase(caseSensitive)]
    }

    override fun toMap(): Map<String, RegisteredCommand> {
        return map.toMap()
    }

}

object ExactSelector : CommandSelector by GenericExactSelector(true) {
    object CaseInsensitive : CommandSelector by GenericExactSelector(false)
}