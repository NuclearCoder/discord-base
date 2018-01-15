# Discord Base (name subject to change)
Base code for Discord bots written in Kotlin

TODO:
-write README
-write wiki
-unit tests



Example of simple self-contained bot:
```kotlin
fun main(args: Array<String>) {
    runBot {
        configName = "examplebot.cfg"
        
        shardedWith(2)
        
        commands(prefix = "~") {
            it["exit"] = ExitCommand
            it("root") {
                it["first"] = Command {
                    LOGGER.info("First subcommand")
                }
            }
        }
        
        messageHandler { event ->
            if ("ping" in event.message.contentDisplay) {
                event.message.replyAsync { "Pong!" }
            }
        }
    }
}
