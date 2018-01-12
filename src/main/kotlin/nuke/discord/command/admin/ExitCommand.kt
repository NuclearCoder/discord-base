package nuke.discord.command.admin

import nuke.discord.command.meta.CommandContext
import nuke.discord.command.meta.command.OwnerRestrictedCommand

object ExitCommand : OwnerRestrictedCommand() {

    override fun onInvoke(context: CommandContext) {
        context.reply(":wave:", "shutting down...")
        context.bot.terminate()
    }

}