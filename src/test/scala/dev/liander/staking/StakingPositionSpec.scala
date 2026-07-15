package dev.liander.staking

import org.scalatest.funsuite.AnyFunSuite

class StakingPositionSpec extends AnyFunSuite {

  // staked=1000, rate=1e9 => reward = 1000 * elapsed * 1e9 / 1e12 = elapsed
  private val staked = 1000L
  private val rate = 1000000000L

  test("claim then claim accrues from the reset point") {
    val pos = new StakingPosition(staked, rate, 0L)
    assert(pos.claim(100L) == 100L)
    assert(pos.claim(250L) == 150L)
  }

  test("pending is non-mutating") {
    val pos = new StakingPosition(staked, rate, 0L)
    assert(pos.pending(100L) == 100L)
    assert(pos.pending(100L) == 100L)
    assert(pos.claim(100L) == 100L)
  }

  test("reward is proportional to elapsed time") {
    assert(StakingPosition.rewardFor(staked, 50L, rate) == 50L)
    assert(StakingPosition.rewardFor(staked, 100L, rate) == 100L)
  }

  test("zero stake or zero time yields nothing") {
    assert(StakingPosition.rewardFor(0L, 100L, rate) == 0L)
    assert(StakingPosition.rewardFor(staked, 0L, rate) == 0L)
  }

  test("rejects negative inputs") {
    assertThrows[IllegalArgumentException](new StakingPosition(-1L, rate, 0L))
  }

  test("rejects time going backwards") {
    val pos = new StakingPosition(staked, rate, 100L)
    assertThrows[IllegalArgumentException](pos.claim(50L))
  }
}
