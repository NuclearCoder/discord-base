package nuke.discord.command.test

import nuke.discord.LOGGER
import nuke.discord.command.meta.CommandContext
import nuke.discord.command.meta.command.Command

object TestCommand : Command() {
    override fun onInvoke(context: CommandContext) {
        LOGGER.info("Test command.")

        context.reply("Test")
    }
}