package nuke.discord.command.meta.selectors

import nuke.discord.command.meta.registry.RegisteredCommand

interface CommandSelector {

    fun add(command: RegisteredCommand)
    fun select(name: String): RegisteredCommand?

    fun toMap(): Map<String, RegisteredCommand>

}