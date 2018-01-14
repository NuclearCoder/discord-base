package nuke.discord

import nuke.discord.bot.runBot
import nuke.discord.command.admin.ExitCommand
import nuke.discord.command.test.TestCommand

fun main(args: Array<String>) {
    runBot {
        configName = "nukebot.cfg"

        //shardedWith(3)
        commands {
            it["stop"] = ExitCommand
            it["test"] = TestCommand
        }
    }
}
