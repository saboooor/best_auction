package com.hacklab.best_auction.managers

import com.hacklab.best_auction.Main
import com.hacklab.best_auction.data.MailItem
import com.hacklab.best_auction.database.MailBox
import com.hacklab.best_auction.utils.ItemUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class MailManager(private val plugin: Main) {

    fun sendMail(playerUuid: UUID, playerName: String, item: ItemStack, reason: String) {
        transaction {
            MailBox.insert {
                it[MailBox.playerUuid] = playerUuid.toString()
                it[MailBox.playerName] = playerName
                it[itemData] = ItemUtils.serializeItemStack(item)
                it[MailBox.reason] = reason
            }
        }
    }

    fun getPlayerMail(player: Player): List<MailItem> {
        return transaction {
            MailBox.select { 
                (MailBox.playerUuid eq player.uniqueId.toString()) and 
                not(MailBox.isCollected) 
            }.map { row ->
                val item = ItemUtils.deserializeItemStack(row[MailBox.itemData])
                if (item != null) {
                    MailItem(
                        id = row[MailBox.id].value,
                        playerUuid = UUID.fromString(row[MailBox.playerUuid]),
                        playerName = row[MailBox.playerName],
                        itemStack = item,
                        reason = row[MailBox.reason],
                        createdAt = row[MailBox.createdAt]
                    )
                } else {
                    null
                }
            }.filterNotNull()
        }
    }

    fun collectMail(player: Player, mailId: Int): Boolean {
        return transaction {
            val mailItem = MailBox.select { 
                (MailBox.id eq mailId) and 
                (MailBox.playerUuid eq player.uniqueId.toString()) and 
                not(MailBox.isCollected) 
            }.singleOrNull()

            if (mailItem == null) {
                player.sendMessage("§cMail item not found!")
                return@transaction false
            }

            val item = ItemUtils.deserializeItemStack(mailItem[MailBox.itemData])
            if (item == null) {
                player.sendMessage("§cFailed to restore item!")
                return@transaction false
            }

            if (!canFitItem(player, item)) {
                player.sendMessage("§cInventory full! Clear some space and try again.")
                return@transaction false
            }

            val leftover = player.inventory.addItem(item)
            if (leftover.isNotEmpty()) {
                val leftoverAmount = leftover.values.sumOf { it.amount }
                if (leftoverAmount < item.amount) {
                    val remainingItem = item.clone().apply { amount = leftoverAmount }
                    MailBox.update({ MailBox.id eq mailId }) {
                        it[itemData] = ItemUtils.serializeItemStack(remainingItem)
                    }
                }
                player.sendMessage("§cInventory full! Clear some space and try again.")
                return@transaction false
            }

            MailBox.update({ MailBox.id eq mailId }) {
                it[isCollected] = true
            }

            player.sendMessage("§aItem collected!")
            true
        }
    }

    private fun canFitItem(player: Player, item: ItemStack): Boolean {
        if (item.type.isAir || item.amount <= 0) return true
        val maxStack = minOf(item.maxStackSize, player.inventory.maxStackSize)
        var space = 0
        for (slot in player.inventory.storageContents) {
            if (slot == null || slot.type.isAir) {
                space += maxStack
            } else if (slot.isSimilar(item)) {
                space += (maxStack - slot.amount).coerceAtLeast(0)
            }
            if (space >= item.amount) return true
        }
        return false
    }

    fun openMailBox(player: Player) {
        com.hacklab.best_auction.ui.MailDialog.open(player, plugin)
    }

    fun handleMailBoxClick(player: Player, clickedItem: ItemStack): Boolean {
        if (clickedItem.type == Material.ARROW) {
            // Back to main auction GUI
            return true // Let caller handle this
        }
        
        if (clickedItem.type == Material.PAPER) {
            // Empty mailbox indicator - do nothing
            return false
        }
        
        val meta = clickedItem.itemMeta ?: return false
        val lore = meta.lore ?: return false
        
        // Extract mail ID from lore
        val idLine = lore.find { it.startsWith("§8ID: ") }
        val mailId = idLine?.replace("§8ID: ", "")?.toIntOrNull() ?: return false
        
        // Collect the mail
        if (collectMail(player, mailId)) {
            // Refresh mailbox after successful collection
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                openMailBox(player)
            }, 1L)
        }
        
        return false
    }
}