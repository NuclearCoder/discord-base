package nuke.discord.command.admin

import nuke.discord.command.meta.CommandContext
import nuke.discord.command.meta.command.Command
import nuke.discord.command.meta.command.PermLevel

object ExitCommand : Command(
        description = "Shuts down the bot.",
        requiredPermission = PermLevel.BOT_OWNER
) {

    override fun onInvoke(context: CommandContext) {
        context.reply(":wave:", "shutting down...")
        context.bot.terminate()
    }

}