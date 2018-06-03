package nuke.discord.command.meta.selectors

import nuke.discord.command.meta.registry.RegisteredCommand

object ExactSelector : CommandSelector {

    private val map = mutableMapOf<String, RegisteredCommand>()

    override fun add(command: RegisteredCommand) {
        map[command.name] = command
    }

    override fun select(name: String): RegisteredCommand? {
        return map[name]
    }

    override fun toMap(): Map<String, RegisteredCommand> {
        return map.toMap()
    }

}