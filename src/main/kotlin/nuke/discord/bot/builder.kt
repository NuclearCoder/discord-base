package nuke.discord.bot

import nuke.discord.bot.basic.NukeBotBasic
import nuke.discord.bot.sharded.NukeBotSharded
import nuke.discord.command.meta.registry.CommandRegistry
import nuke.discord.util.Config


typealias CommandBuilder = (CommandRegistry.RegistryBuilder) -> Unit


fun runBot(configName: String,
                sharded: Boolean = false,
                shardCount: Int = 0,
                builder: CommandBuilder): NukeBot =

        if (sharded)
            runBotBasic(configName, builder)
        else
            runBotSharded(configName, shardCount, builder)


fun runBotBasic(configName: String, builder: CommandBuilder) =
        NukeBotBasic(Config(configName), builder)

fun runBotSharded(configName: String, shardCount: Int, builder: CommandBuilder) =
        NukeBotSharded(Config(configName), shardCount, builder)
