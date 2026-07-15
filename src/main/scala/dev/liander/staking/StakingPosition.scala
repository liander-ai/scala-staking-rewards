package dev.liander.staking

/**
 * Time-based staking reward accrual in Scala — the same protocol I built on
 * Solana (Anchor), EVM (Solidity), Java, and Kotlin:
 * `reward = staked * elapsed * rewardRate / 1e12`, with `claim` resetting the clock.
 *
 * Uses `BigInt` for the intermediate product to avoid overflow. Pure and
 * deterministic, tested with ScalaTest.
 */
object StakingPosition {

  /** Fixed-point scale (1e12), matching the Solana/EVM implementations. */
  val Scale: Long = 1000000000000L

  /** Reward accrued by `staked` tokens over `elapsed` seconds at `rate`. */
  def rewardFor(staked: Long, elapsed: Long, rate: Long): Long = {
    require(staked >= 0 && elapsed >= 0 && rate >= 0, "inputs must be non-negative")
    (BigInt(staked) * BigInt(elapsed) * BigInt(rate) / BigInt(Scale)).bigInteger.longValueExact()
  }
}

final class StakingPosition(staked: Long, rewardRate: Long, startTime: Long) {
  require(staked >= 0 && rewardRate >= 0, "staked and rewardRate must be non-negative")

  private var lastTime: Long = startTime
  private var accrued: Long = 0L

  private def elapsedSince(now: Long): Long = {
    require(now >= lastTime, "time must not go backwards")
    now - lastTime
  }

  /** Unclaimed rewards as of `now`, without mutating state. */
  def pending(now: Long): Long =
    accrued + StakingPosition.rewardFor(staked, elapsedSince(now), rewardRate)

  /** Settle accrued rewards up to `now`, resetting the clock, and return them. */
  def claim(now: Long): Long = {
    accrued += StakingPosition.rewardFor(staked, elapsedSince(now), rewardRate)
    lastTime = now
    val payout = accrued
    accrued = 0L
    payout
  }
}
