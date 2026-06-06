# 2. Merge with Gugu Project

Date: 2025-12-27
Last update: 2026-01-30

## Status

Done

## Context

Because original development of Slimefun has been very slow in the past months, Gugu Project started to add
new features to their fork, they also stopped to merge changes from the original Slimefun a few months before
its official abandonment.

### Gugu Project's additions

- New storage types (SQLite, MySQL, PostgreSQL)
- A few new commands:
  - /sf id
  - /sf reload
  - /sf blockdata
  - /sf banitem
  - /sf unbanitem
  - /sf cleardata

## Technical details

The merge happened in [commit `68d500f`](https://github.com/Slimefun-United/Slimefun-United/commit/68d500f2a98af5df7796fa8ac1d4ee15db57c405).

The merge was between the latest commit from the `experimental` branch of original Slimefun and the Gugu Project's `dev`.
It seemed like most conflicts were easy to resolve, however there were a few problematic classes, which needed more attention.
I tried to test all features of Slimefun after it the best I could, however it is likely there still are some minor bugs caused by it.

This basically means Slimefun has now one, common point in history
between the official experimental branch (with some updates and changes on top of it)
and Gugu Project's version (but with English translation on top if it).

The merge commit was originally planned to be split into a few smaller chunks
but due to Git's design, I just wasn't able to get it to work in a satisfying way.

Instead, I'll list the way it was supposed to be split:

- io.github.thebusybiscuit.slimefun4:
  - API
  - Core
  - Implementation
  - Integrations
  - Storage
  - Utils
- me.mrCookieSlime
- city.norain.slimefun4
- com.xzavier0722.mc.plugin.slimefun4
- net.guizhanss.slimefun4
- Resources
- Remaining files in project's root

I list it here since this might be of some use in the future and even if it's not,
I don't want it to go to waste.

## Bugs

Checked issues have been resolved.

### Priority

- [x] Generators do not work!
- [x] Elemental staffs do not work (Multitool does)
- [ ] Data conversion gives warnings

### Minor

- [ ] When an Auto Enchanter is processing items, breaking it will drop nothing
- [ ] When an Auto Enchanter is processing items, turning the server off during that time will remove these items and progress
- [ ] When all slots in a generator are full, it's possible to put items into output using Shift + click on a specific item

## Important files

Problematic files during the merge, which are likely to cause issues (from high risk to low risk):

- `src/main/java/io/github/thebusybiscuit/slimefun4/implementation/setup/SlimefunItemSetup.java`

- `src\main\java\io\...\thebusybiscuit\slimefun4\utils\SlimefunUtils.java`
- `src\main\java\io\...\slimefun4\api\player\PlayerProfile.java`
- `src\main\java\io...\thebusybiscuit\slimefun4\core\SlimefunRegistry.java`
- `src\main\java\...slimefun4\core\commands\SlimefunTabCompleter.java`
- `src\main\java\io\github...slimefun4\core\networks\cargo\ItemFilter.java`
- `src\main\java\io\...slimefun4\core\networks\cargo\CargoUtils.java`
- `src\main\java\io\...\core\networks\cargo\CargoNetworkTask.java`
- `src\main\java\io\github...slimefun4\core\networks\cargo\CargoNet.java`
- `src\main\java\...\implementation\listeners\DebugFishListener.java`
- `src\main\java\io\...\implementation\items\tools\ExplosiveTool.java`
- `src\main\java\io\...\items\electric\gadgets\MultiTool.java`
- `src\main\java\...\machines\accelerators\TreeGrowthAccelerator.java`

- `src\main\java\io\...\implementation\tasks\armor\RadiationTask.java`
- `src\main\java\io\...\core\networks\cargo\ItemStackAndInteger.java`
