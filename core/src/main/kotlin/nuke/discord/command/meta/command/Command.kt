package nuke.discord.command.meta.command

import nuke.discord.command.meta.CommandContext
import nuke.discord.util.discord.hasSufficientPermissions

abstract class Command(
        val description: String = defaultDescription,
        val requiredPermission: PermLevel = defaultPermission
) {

    companion object {

        const val defaultDescription = "No description."
        val defaultPermission = PermLevel.USER

        inline operator fun invoke(description: String = defaultDescription,
                                   requiredPermission: PermLevel = defaultPermission,
                                   crossinline block: Command.(CommandContext) -> Unit)
                = object : Command(description, requiredPermission) {
                    override fun onInvoke(context: CommandContext) = block(context)
                }
    }

    abstract fun onInvoke(context: CommandContext)

    protected fun CommandContext.hasSufficientPermission(permission: PermLevel) =
            message.member.hasSufficientPermissions(this, permission)

}