package nuke.discord.command.meta.registry

import nuke.discord.command.meta.command.Command

sealed class RegisteredCommand(val name: String, val command: Command) {

    internal class Final(name: String, command: Command)
        : RegisteredCommand(name, command)

    internal class Branch(name: String, command: Command, val registry: CommandRegistry)
        : RegisteredCommand(name, command)

}