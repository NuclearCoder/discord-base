package nuke.discord.command.meta.command

import nuke.discord.command.meta.CommandContext
import nuke.discord.util.discord.hasSufficientPermissions

abstract class Command(val requiredPermission: PermLevel = PermLevel.USER) {

    companion object {
        inline operator fun invoke(perm: PermLevel = PermLevel.USER,
                                   crossinline block: Command.(CommandContext) -> Unit)
                = object : Command(perm) {
                    override fun onInvoke(context: CommandContext) = block(context)
                }
    }

    abstract fun onInvoke(context: CommandContext)

    protected fun CommandContext.hasSufficientPermission(permission: PermLevel) =
            message.member.hasSufficientPermissions(this, permission)

}