# scala-staking-rewards

**Time-based staking reward accrual in Scala** — the same protocol I built on
Solana (Anchor/Rust), EVM (Solidity), Java, and Kotlin:
`reward = staked · elapsed · rewardRate / 1e12`, with `claim` resetting the accrual clock.

A `StakingPosition` class plus a `StakingPosition` companion `object` holding the pure
`rewardFor` calculation (`BigInt` intermediate product to avoid overflow, `require(...)`
for validation). Tested with **ScalaTest** (`AnyFunSuite`).

## Tests (`src/test/scala/...`)

- `claim` then `claim` accrues from the reset point (100, then 150),
- `pending` is read-only,
- reward is proportional to elapsed time,
- zero stake / zero time yields nothing,
- negative inputs and backwards time are rejected (`assertThrows`).

## Run it

```bash
mvn -B test
```

Requires JDK 17+ and Maven (Scala is compiled via `scala-maven-plugin`; tests run via
`scalatest-maven-plugin`). CI (`.github/workflows/ci.yml`) provisions Temurin 17 and
runs `mvn -B test`.

## License

MIT — see [LICENSE](LICENSE).
