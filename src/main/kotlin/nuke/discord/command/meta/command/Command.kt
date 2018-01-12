package nuke.discord.command.meta.command

import nuke.discord.command.meta.CommandContext
import nuke.discord.util.discord.hasSufficientPermissions

abstract class Command(val requiredPermission: PermissionLevel = PermissionLevel.User) {

    abstract fun onInvoke(context: CommandContext)

    protected fun CommandContext.hasSufficientPermission(permission: PermissionLevel) =
            message.member.hasSufficientPermissions(this, permission)

}