package com.hacklab.best_auction.ui

import com.hacklab.best_auction.Main
import com.hacklab.best_auction.data.AuctionItem
import com.hacklab.best_auction.utils.ItemUtils
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import java.time.Duration
import java.util.UUID
import java.util.function.Consumer

object BidDialog {
    fun open(player: Player, plugin: Main, auction: AuctionItem, changing: Boolean = false,
             initial: String = "", error: String? = null) {
        if (!player.isOnline) return
        fun message(key: String, vararg args: Any) = plugin.langManager.getMessage(player, key, *args)
        fun price(amount: Long) = ItemUtils.formatPriceWithCurrency(amount, plugin.getEconomyProvider(), plugin)
        val title = message(if (changing) "ui.change_bid_header" else "ui.bid_prompt_header")
        val label = message(if (changing) "ui.enter_new_bid_amount" else "ui.enter_bid_amount")
        val details = buildList {
            error?.let { add("§c$it") }
            add("${message("ui.seller")}: ${auction.sellerName}")
            add("${message("ui.current_highest_bid")}: ${price(auction.currentPrice)}")
            if (changing) add("${message("ui.your_current_bid")}: ${price(auction.playerBidAmount ?: 0)}")
            if (auction.currentPrice < Long.MAX_VALUE) add("${message("ui.minimum_bid")}: ${price(auction.currentPrice + 1)}")
            auction.buyoutPrice?.let { add("${message("ui.buyout_price")}: ${price(it)}") }
            add(message("time.remaining", ItemUtils.formatTimeRemaining(auction.expiresAt, plugin.langManager, player)))
            add("${message("auction.bid_format_example")}: 1000, 1k, 1.5k, 1m")
        }.joinToString("\n")
        val submit: (String) -> Unit = { input ->
            val amount = ItemUtils.parseAmount(input.trim())
            when {
                amount == null || amount <= 0 -> open(player, plugin, auction, changing, input.take(32), message("auction.invalid_bid_format"))
                amount <= auction.currentPrice -> open(player, plugin, auction, changing, input.take(32),
                    message("auction.bid_too_low", price(if (auction.currentPrice == Long.MAX_VALUE) Long.MAX_VALUE else auction.currentPrice + 1)))
                else -> plugin.auctionManager.placeBid(player, auction.id, amount)
            }
        }
        if (bedrock(player, plugin, title, details, label, initial, submit)) return
        val serializer = LegacyComponentSerializer.legacySection()
        val bid = ActionButton.create(Component.text(message("ui.submit_bid")), null, 150,
            DialogAction.customClick({ response, audience ->
                if (audience is Player && audience.uniqueId == player.uniqueId) {
                    val input = response.getText("amount") ?: ""
                    schedule(player, plugin) { submit(input) }
                }
            }, ClickCallback.Options.builder().uses(1).lifetime(Duration.ofMinutes(5)).build()))
        player.showDialog(Dialog.create { builder ->
            builder.empty().base(DialogBase.builder(Component.text(title)).pause(false)
                .body(listOf(DialogBody.plainMessage(serializer.deserialize(details), 350)))
                .inputs(listOf(DialogInput.text("amount", Component.text(label)).maxLength(32).initial(initial).width(300).build()))
                .build()).type(DialogType.multiAction(listOf(bid),
                    ActionButton.create(Component.text(message("ui.close")), null, 150, null), 1))
        })
    }

    private fun schedule(player: Player, plugin: Main, action: () -> Unit) {
        if (plugin.isEnabled) plugin.server.scheduler.runTask(plugin, Runnable { if (player.isOnline) action() })
    }

    private fun bedrock(player: Player, plugin: Main, title: String, details: String, label: String,
                        initial: String, submit: (String) -> Unit): Boolean {
        val floodgate = plugin.server.pluginManager.getPlugin("floodgate") ?: return false
        if (!floodgate.isEnabled) return false
        var detected = false
        try {
            fun type(name: String) = Class.forName(name, true, floodgate.javaClass.classLoader)
            val apiClass = type("org.geysermc.floodgate.api.FloodgateApi")
            val api = apiClass.getMethod("getInstance").invoke(null)
            detected = apiClass.getMethod("isFloodgatePlayer", UUID::class.java).invoke(api, player.uniqueId) as Boolean
            if (!detected) return false
            val formClass = type("org.geysermc.cumulus.form.CustomForm")
            val builderClass = type("org.geysermc.cumulus.form.CustomForm\$Builder")
            val genericBuilder = type("org.geysermc.cumulus.form.util.FormBuilder")
            val builder = formClass.getMethod("builder").invoke(null)
            genericBuilder.getMethod("title", String::class.java).invoke(builder, title)
            builderClass.getMethod("label", String::class.java).invoke(builder, details)
            builderClass.getMethod("input", String::class.java, String::class.java, String::class.java)
                .invoke(builder, label, "1000, 1k, 1.5k", initial)
            val responseClass = type("org.geysermc.cumulus.response.CustomFormResponse")
            genericBuilder.getMethod("validResultHandler", Consumer::class.java).invoke(builder, Consumer<Any> { response ->
                try {
                    val input = responseClass.getMethod("asInput", Int::class.javaPrimitiveType).invoke(response, 1) as String
                    schedule(player, plugin) { submit(input) }
                } catch (e: ReflectiveOperationException) {
                    plugin.logger.warning("Invalid Bedrock bid response: ${e.message}")
                }
            })
            val sent = apiClass.getMethod("sendForm", UUID::class.java, genericBuilder).invoke(api, player.uniqueId, builder) as Boolean
            if (!sent) fallback(player, auctionCommand = "/ah bid <id> <amount>")
            return true
        } catch (e: ReflectiveOperationException) {
            plugin.logger.warning("Floodgate bid form unavailable: ${e.message}")
        } catch (e: LinkageError) {
            plugin.logger.warning("Floodgate bid form unavailable: ${e.message}")
        }
        if (detected) fallback(player, "/ah bid <id> <amount>")
        return detected
    }

    private fun fallback(player: Player, auctionCommand: String) {
        player.sendMessage("Bid forms are unavailable. Use $auctionCommand.")
    }
}
