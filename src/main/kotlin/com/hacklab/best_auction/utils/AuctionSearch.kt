package com.hacklab.best_auction.utils

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.inventory.ItemStack
import java.util.Locale

internal object AuctionSearch {
    fun matches(item: ItemStack, term: String): Boolean {
        val query = normalize(term)
        val materialName = normalize(item.type.name)
        val customName = item.itemMeta?.displayName()?.let {
            normalize(PlainTextComponentSerializer.plainText().serialize(it))
        }.orEmpty()
        return materialName.contains(query) || customName.contains(query)
    }

    internal fun normalize(value: String): String = value.trim().lowercase(Locale.ROOT)
        .replace('_', ' ').replace(Regex("\\s+"), " ")
}
