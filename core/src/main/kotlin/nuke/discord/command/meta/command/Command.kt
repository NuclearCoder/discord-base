package nuke.discord.command.meta.command

import nuke.discord.command.meta.CommandContext
import nuke.discord.util.discord.hasSufficientPermissions

abstract class Command(val requiredPermission: PermLevel = PermLevel.USER) {

    abstract fun onInvoke(context: CommandContext)

    protected fun CommandContext.hasSufficientPermission(permission: PermLevel) =
            message.member.hasSufficientPermissions(this, permission)

}