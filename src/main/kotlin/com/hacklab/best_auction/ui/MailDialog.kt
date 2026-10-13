package com.hacklab.best_auction.ui

import com.hacklab.best_auction.Main
import com.hacklab.best_auction.data.MailItem
import com.hacklab.best_auction.utils.ItemUtils
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import java.time.Duration
import java.util.UUID
import java.util.function.Consumer

object MailDialog {
    private const val PAGE_SIZE = 12
    private data class Button(val label: String, val action: () -> Unit)

    fun open(player: Player, plugin: Main, page: Int = 0) {
        val mail = plugin.mailManager.getPlayerMail(player)
        val lastPage = ((mail.size - 1).coerceAtLeast(0) / PAGE_SIZE)
        val current = page.coerceIn(0, lastPage)
        val buttons = mail.drop(current * PAGE_SIZE).take(PAGE_SIZE).map { item ->
            Button("${name(item)} ×${item.itemStack.amount}") { details(player, plugin, item, current) }
        }.toMutableList()
        if (current > 0) buttons += Button(message(player, plugin, "ui.previous_page")) { open(player, plugin, current - 1) }
        if (current < lastPage) buttons += Button(message(player, plugin, "ui.next_page")) { open(player, plugin, current + 1) }
        buttons += Button(message(player, plugin, "ui.back")) { AuctionUI.openMainUI(player, plugin) }
        val body = if (mail.isEmpty()) message(player, plugin, "mail.no_mail")
            else message(player, plugin, "ui.page_info", "${current + 1}", "${lastPage + 1}")
        show(player, plugin, message(player, plugin, "ui.mailbox"), body, buttons)
    }

    private fun details(player: Player, plugin: Main, item: MailItem, page: Int) {
        val body = "${name(item)} ×${item.itemStack.amount}\n" +
            message(player, plugin, "mail.mail_reason", item.reason) + "\n" +
            message(player, plugin, "mail.mail_date", ItemUtils.formatDate(item.createdAt, plugin))
        show(player, plugin, message(player, plugin, "ui.mailbox"), body, listOf(
            Button(message(player, plugin, "mail.collect_button")) {
                plugin.mailManager.collectMail(player, item.id)
                open(player, plugin, page)
            },
            Button(message(player, plugin, "ui.back")) { open(player, plugin, page) }
        ))
    }

    private fun name(item: MailItem): String = item.itemStack.itemMeta?.displayName()?.let {
        PlainTextComponentSerializer.plainText().serialize(it)
    } ?: item.itemStack.type.name.lowercase().replace('_', ' ')

    private fun message(player: Player, plugin: Main, key: String, vararg args: Any) =
        plugin.langManager.getMessage(player, key, *args)

    private fun schedule(player: Player, plugin: Main, action: () -> Unit) {
        if (plugin.isEnabled) plugin.server.scheduler.runTask(plugin, Runnable { if (player.isOnline) action() })
    }

    private fun show(player: Player, plugin: Main, title: String, body: String, buttons: List<Button>) {
        if (!player.isOnline) return
        player.closeInventory()
        if (bedrock(player, plugin, title, body, buttons)) return
        val actions = buttons.map { button ->
            ActionButton.create(Component.text(button.label), null, 300, DialogAction.customClick({ _, audience ->
                if (audience is Player && audience.uniqueId == player.uniqueId) schedule(player, plugin, button.action)
            }, ClickCallback.Options.builder().uses(1).lifetime(Duration.ofMinutes(5)).build()))
        }
        player.showDialog(Dialog.create { builder ->
            builder.empty().base(DialogBase.builder(Component.text(title)).pause(false)
                .body(listOf(DialogBody.plainMessage(Component.text(body), 350))).build())
                .type(DialogType.multiAction(actions,
                    ActionButton.create(Component.text(message(player, plugin, "ui.close")), null, 150, null), 1))
        })
    }

    private fun bedrock(player: Player, plugin: Main, title: String, body: String, buttons: List<Button>): Boolean {
        val floodgate = plugin.server.pluginManager.getPlugin("floodgate") ?: return false
        if (!floodgate.isEnabled) return false
        var detected = false
        try {
            fun type(name: String) = Class.forName(name, true, floodgate.javaClass.classLoader)
            val apiClass = type("org.geysermc.floodgate.api.FloodgateApi")
            val api = apiClass.getMethod("getInstance").invoke(null)
            detected = apiClass.getMethod("isFloodgatePlayer", UUID::class.java).invoke(api, player.uniqueId) as Boolean
            if (!detected) return false
            val formClass = type("org.geysermc.cumulus.form.SimpleForm")
            val builderClass = type("org.geysermc.cumulus.form.SimpleForm\$Builder")
            val genericBuilder = type("org.geysermc.cumulus.form.util.FormBuilder")
            val builder = formClass.getMethod("builder").invoke(null)
            genericBuilder.getMethod("title", String::class.java).invoke(builder, title)
            builderClass.getMethod("content", String::class.java).invoke(builder, body)
            buttons.forEach { builderClass.getMethod("button", String::class.java).invoke(builder, it.label) }
            builderClass.getMethod("button", String::class.java).invoke(builder, message(player, plugin, "ui.close"))
            val responseClass = type("org.geysermc.cumulus.response.SimpleFormResponse")
            genericBuilder.getMethod("validResultHandler", Consumer::class.java).invoke(builder, Consumer<Any> { response ->
                try {
                    val selected = responseClass.getMethod("clickedButtonId").invoke(response) as Int
                    buttons.getOrNull(selected)?.let { schedule(player, plugin, it.action) }
                } catch (e: ReflectiveOperationException) {
                    plugin.logger.warning("Invalid Bedrock mailbox response: ${e.message}")
                }
            })
            if (!(apiClass.getMethod("sendForm", UUID::class.java, genericBuilder).invoke(api, player.uniqueId, builder) as Boolean)) fallback(player)
            return true
        } catch (e: ReflectiveOperationException) {
            plugin.logger.warning("Floodgate mailbox form unavailable: ${e.message}")
        } catch (e: LinkageError) {
            plugin.logger.warning("Floodgate mailbox form unavailable: ${e.message}")
        }
        if (detected) fallback(player)
        return detected
    }

    private fun fallback(player: Player) {
        player.sendMessage("Mailbox forms are unavailable. Use /ah mail collect <id> to collect an item.")
    }
}
