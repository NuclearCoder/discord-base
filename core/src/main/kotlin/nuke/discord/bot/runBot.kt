package nuke.discord.bot

fun runBot(init: BotBuilder.() -> Unit) =
        BotBuilder().apply(init).build()

