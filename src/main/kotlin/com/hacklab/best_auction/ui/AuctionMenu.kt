package com.hacklab.best_auction.ui

import com.hacklab.best_auction.Main
import com.hacklab.best_auction.data.AuctionCategory
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.`object`.ObjectContents
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration
import java.util.UUID
import java.util.function.Consumer

/** One set of menu actions, rendered as a Paper dialog or an optional Floodgate form. */
object AuctionMenu {
    private enum class Icon(val sprite: String, val bedrockPath: String) {
        ALL("item/emerald", "textures/items/emerald"),
        BLOCKS("block/stone", "textures/blocks/stone"),
        FOOD("item/apple", "textures/items/apple"),
        EQUIPMENT("item/diamond_sword", "textures/items/diamond_sword"),
        ENCHANTMENTS("item/enchanted_book", "textures/items/book_enchanted"),
        OTHERS("item/lava_bucket", "textures/items/bucket_lava"),
        LISTINGS("item/paper", "textures/items/paper"),
        BIDS("item/gold_ingot", "textures/items/gold_ingot"),
        MAIL("item/book", "textures/items/book_normal"),
        SEARCH("item/spyglass", "textures/items/spyglass"),
        CLOSE("item/barrier", "textures/items/barrier")
    }

    private data class Entry(val label: String, val icon: Icon, val action: () -> Unit)

    private fun categoryIcon(category: AuctionCategory): Icon = when (category) {
        AuctionCategory.ALL -> Icon.ALL
        AuctionCategory.BLOCKS -> Icon.BLOCKS
        AuctionCategory.FOOD -> Icon.FOOD
        AuctionCategory.WEAPONS_EQUIPMENT -> Icon.EQUIPMENT
        AuctionCategory.ENCHANTMENTS -> Icon.ENCHANTMENTS
        AuctionCategory.OTHERS -> Icon.OTHERS
    }

    private fun buttonLabel(label: String, icon: Icon): Component {
        // Minecraft 1.21.11 moved item sprites to a separate atlas (same as Lumistones).
        val version = Bukkit.getMinecraftVersion().split(".").map { it.toIntOrNull() ?: 0 }
        val separateItems = version.getOrElse(0) { 1 } > 1 ||
            version.getOrElse(1) { 0 } > 21 ||
            (version.getOrElse(1) { 0 } == 21 && version.getOrElse(2) { 0 } >= 11)
        val atlas = if (icon.sprite.startsWith("item/") && separateItems) "minecraft:items" else "minecraft:blocks"
        return Component.`object`(ObjectContents.sprite(Key.key(atlas), Key.key("minecraft:${icon.sprite}")))
            .append(Component.text(" $label"))
    }

    fun open(player: Player, plugin: Main) {
        fun message(key: String) = plugin.langManager.getMessage(player, key)
        val activeListings = plugin.auctionManager.getActiveListings()
        val populatedCategories = activeListings.map { it.category }.toSet()
        val categories = AuctionCategory.values().filter { category ->
            if (category == AuctionCategory.ALL) activeListings.isNotEmpty()
            else AuctionCategory.categoryDbNames[category].orEmpty().any { it in populatedCategories }
        }.map { category ->
            Entry(AuctionUI.getCategoryDisplayName(category, plugin, player), categoryIcon(category)) {
                AuctionUI.openCategoryUI(player, plugin, category)
            }
        }
        val entries = categories.toMutableList()
        entries += Entry(message("ui.your_auctions"), Icon.LISTINGS) { AuctionUI().openMyListingsUI(player, plugin) }
        entries += Entry(message("ui.my_bids"), Icon.BIDS) { AuctionUI.openMyBidsUI(player, plugin) }
        entries += Entry(message("ui.mailbox"), Icon.MAIL) { plugin.mailManager.openMailBox(player) }
        entries += Entry(message("ui.search"), Icon.SEARCH) {
            showBedrockSearch(player, plugin)
        }
        val title = message("ui.auction_house")
        player.closeInventory()
        if (showBedrock(player, plugin, title, entries)) return
        fun spacer() = ActionButton.create(Component.text(" "), null, 1, null)
        fun button(entry: Entry): ActionButton {
            return ActionButton.create(buttonLabel(entry.label, entry.icon), null, 150,
                DialogAction.customClick({ view, audience ->
                    if (audience is Player && audience.uniqueId == player.uniqueId) {
                        if (entry.icon == Icon.SEARCH) {
                            val term = view.getText("search") ?: ""
                            schedule(player, plugin) { search(player, plugin, term) }
                        } else {
                            schedule(player, plugin, entry.action)
                        }
                    }
                }, ClickCallback.Options.builder().uses(1).lifetime(Duration.ofMinutes(5)).build()))
        }
        val buttons = mutableListOf<ActionButton>()
        // Categories and personal actions share the native three-column grid.
        buttons += listOf(spacer(), button(entries.single { it.icon == Icon.SEARCH }), spacer())
        buttons += listOf(spacer(), spacer(), spacer())
        categories.chunked(3).forEach { row ->
            buttons += (0..2).map { index -> row.getOrNull(index)?.let { button(it) } ?: spacer() }
        }
        if (categories.isNotEmpty()) buttons += listOf(spacer(), spacer(), spacer())
        buttons += entries.filter { it.icon in setOf(Icon.LISTINGS, Icon.BIDS, Icon.MAIL) }.map { button(it) }
        player.showDialog(Dialog.create { builder ->
            builder.empty().base(DialogBase.builder(Component.text(title)).pause(false)
                .inputs(listOf(DialogInput.text("search", Component.text(message("ui.search")))
                    .maxLength(100).width(300).build())).build())
                .type(DialogType.multiAction(buttons, ActionButton.create(buttonLabel(message("ui.close"), Icon.CLOSE), null, 300, null), 3))
        })
    }

    private fun search(player: Player, plugin: Main, input: String) {
        val term = input.trim().take(100)
        if (term.isEmpty()) {
            open(player, plugin)
        } else {
            AuctionUI.openSearchUI(player, plugin, term)
        }
    }

    /** Bedrock simple menus cannot contain text fields, so Search opens a custom form. */
    private fun showBedrockSearch(player: Player, plugin: Main) {
        val floodgate = plugin.server.pluginManager.getPlugin("floodgate") ?: return
        try {
            val loader = floodgate.javaClass.classLoader
            fun type(name: String) = Class.forName(name, true, loader)
            val apiClass = type("org.geysermc.floodgate.api.FloodgateApi")
            val api = apiClass.getMethod("getInstance").invoke(null)
            val formClass = type("org.geysermc.cumulus.form.CustomForm")
            val builderClass = type("org.geysermc.cumulus.form.CustomForm\$Builder")
            val genericBuilder = type("org.geysermc.cumulus.form.util.FormBuilder")
            val builder = formClass.getMethod("builder").invoke(null)
            val label = plugin.langManager.getMessage(player, "ui.search")
            genericBuilder.getMethod("title", String::class.java).invoke(builder, label)
            builderClass.getMethod("input", String::class.java, String::class.java, String::class.java)
                .invoke(builder, label, "", "")
            val responseClass = type("org.geysermc.cumulus.response.CustomFormResponse")
            genericBuilder.getMethod("validResultHandler", Consumer::class.java).invoke(builder, Consumer<Any> { response ->
                try {
                    val term = responseClass.getMethod("asInput", Int::class.javaPrimitiveType).invoke(response, 0) as String
                    schedule(player, plugin) { search(player, plugin, term) }
                } catch (e: ReflectiveOperationException) {
                    plugin.logger.warning("Invalid Bedrock search response: ${e.message}")
                }
            })
            val sent = apiClass.getMethod("sendForm", UUID::class.java, genericBuilder)
                .invoke(api, player.uniqueId, builder) as Boolean
            if (!sent) fallback(player)
        } catch (e: ReflectiveOperationException) {
            plugin.logger.warning("Floodgate search form unavailable: ${e.message}")
            fallback(player)
        } catch (e: LinkageError) {
            plugin.logger.warning("Floodgate search form unavailable: ${e.message}")
            fallback(player)
        }
    }

    private fun schedule(player: Player, plugin: Main, action: () -> Unit) {
        if (!plugin.isEnabled) return
        plugin.server.scheduler.runTask(plugin, Runnable { if (player.isOnline) action() })
    }

    /** Reflection keeps Floodgate/Cumulus optional, as in Lumistones. */
    private fun showBedrock(player: Player, plugin: Main, title: String, entries: List<Entry>): Boolean {
        val floodgate = plugin.server.pluginManager.getPlugin("floodgate") ?: return false
        if (!floodgate.isEnabled) return false
        var bedrock = false
        try {
            val loader = floodgate.javaClass.classLoader
            fun type(name: String) = Class.forName(name, true, loader)
            val apiClass = type("org.geysermc.floodgate.api.FloodgateApi")
            val api = apiClass.getMethod("getInstance").invoke(null)
            bedrock = apiClass.getMethod("isFloodgatePlayer", UUID::class.java).invoke(api, player.uniqueId) as Boolean
            if (!bedrock) return false
            val formClass = type("org.geysermc.cumulus.form.SimpleForm")
            val builderClass = type("org.geysermc.cumulus.form.SimpleForm\$Builder")
            val genericBuilder = type("org.geysermc.cumulus.form.util.FormBuilder")
            val builder = formClass.getMethod("builder").invoke(null)
            genericBuilder.getMethod("title", String::class.java).invoke(builder, title)
            builderClass.getMethod("content", String::class.java).invoke(builder, "")
            val imageType = type("org.geysermc.cumulus.util.FormImage\$Type")
            val pathType = imageType.enumConstants.first { (it as Enum<*>).name == "PATH" }
            val buttonMethod = builderClass.getMethod("button", String::class.java, imageType, String::class.java)
            entries.forEach { buttonMethod.invoke(builder, it.label, pathType, it.icon.bedrockPath) }
            buttonMethod.invoke(builder, plugin.langManager.getMessage(player, "ui.close"), pathType, Icon.CLOSE.bedrockPath)
            val responseClass = type("org.geysermc.cumulus.response.SimpleFormResponse")
            val callback = Consumer<Any> { response ->
                try {
                    val selected = responseClass.getMethod("clickedButtonId").invoke(response) as Int
                    entries.getOrNull(selected)?.let { schedule(player, plugin, it.action) }
                } catch (e: ReflectiveOperationException) {
                    plugin.logger.warning("Invalid Bedrock auction form response: ${e.message}")
                }
            }
            genericBuilder.getMethod("validResultHandler", Consumer::class.java).invoke(builder, callback)
            val sent = apiClass.getMethod("sendForm", UUID::class.java, genericBuilder).invoke(api, player.uniqueId, builder) as Boolean
            if (!sent) fallback(player)
            return true
        } catch (e: ReflectiveOperationException) {
            plugin.logger.warning("Floodgate auction forms unavailable: ${e.message}")
        } catch (e: LinkageError) {
            plugin.logger.warning("Floodgate auction forms unavailable: ${e.message}")
        }
        if (bedrock) fallback(player)
        return bedrock
    }

    private fun fallback(player: Player) {
        player.sendMessage("Auction forms are unavailable. Use /ah search <item>, /ah mail.")
    }
}
