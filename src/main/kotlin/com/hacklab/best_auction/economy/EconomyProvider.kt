package com.hacklab.best_auction.economy

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

/**
 * Abstract interface for the economy system
 * Supports both internal and Vault economies
 */
interface EconomyProvider {
    /**
     * Economy provider name
     */
    val name: String

    /**
     * Currency name (plural)
     */
    fun currencyNamePlural(): String

    /**
     * Format an amount and return its display string
     */
    fun format(amount: Double): String

    /**
     * Check whether a player has at least the specified balance
     */
    fun has(player: Player, amount: Double): Boolean

    /**
     * Check whether an offline player has at least the specified balance
     */
    fun has(player: OfflinePlayer, amount: Double): Boolean

    /**
     * Check whether a player account exists
     */
    fun hasAccount(player: Player): Boolean

    /**
     * Check whether an offline player account exists
     */
    fun hasAccount(player: OfflinePlayer): Boolean

    /**
     * Withdraw money from a player
     * @return true on success
     */
    fun withdrawPlayer(player: Player, amount: Double): Boolean

    /**
     * Withdraw money from an offline player
     * @return true on success
     */
    fun withdrawPlayer(player: OfflinePlayer, amount: Double): Boolean

    /**
     * Deposit money into a player account
     * @return true on success
     */
    fun depositPlayer(player: Player, amount: Double): Boolean

    /**
     * Deposit money into an offline player account
     * @return true on success
     */
    fun depositPlayer(player: OfflinePlayer, amount: Double): Boolean

    /**
     * Get a player's balance
     */
    fun getBalance(player: Player): Double

    /**
     * Get an offline player's balance
     */
    fun getBalance(player: OfflinePlayer): Double
}
