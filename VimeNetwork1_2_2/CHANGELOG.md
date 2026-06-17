# Changelog: VimeNetwork 1.1 → 1.2.2

## ✨ New Features

### Guild System (Clan System)
- Complete guild infrastructure with `VGuild` caching, member management, and 6 GUI menus (`GuildMemberMenu`, `GuildLeaderMenu`, `GuildOfficerMenu`, `GuildPerksMenu`, `GuildColorMenu`)
- `Packet69Guild` with 20+ action types (CREATE, DISBAND, INVITE, ACCEPT, KICK, PROMOTE, DEMOTE, TRANSFER, LEAVE, RENAME, MOTD, DEPOSIT, PARTY, TOP, LIST, SET_TAG, SET_COLOR, MESSAGE, MENU, UPGRADE_PERK, ADM_RELOAD, ADM_ADD_EXP)
- New Commons classes: `GuildStatus` (LEADER/OFFICER/MEMBER), `GuildPerk` (7 upgradeable perks: member slots, daily coin limit, party creation, MOTD, coin multiplier, tag, color), `GuildLeveling` (256-level progression)
- `/guild` (`/g`) command with subcommands: create (150k coins, requires PREMIUM), rename (100k), disband, invite/accept, kick, promote/demote, transfer, leave, deposit, tag, party, motd, menu, list
- `/guildadm` (`/ga`) command (CHIEF+) for searching guilds, renaming, changing tags, adding EXP
- Guild tag displayed in chat before player name: `<guild_tag> player: message`
- Guild cache cleaner runs every 5 minutes (`VGuild.Cleaner`)

### Auto-Restart System
- Scheduled daily restart at 3:00 AM MSK with 5-minute countdown
- Broadcasts at 5m, 3m, 1m, 30s, 20s, 10s, 5-1s remaining
- Fires `ServerRestartEvent` at SCHEDULED, COUNTDOWN, and RESTART stages
- Lobby servers mass-kick players; game servers attempt lobby redirect (or kick if forced)
- Enables whitelist before calling `Bukkit.shutdown()`

### Per-World Tablist
- `PerWorldTablist` listener hides players in different worlds from tablist
- Respects spectator mode visibility
- Controlled by `PER_WORLD_TABLIST` feature flag

### World Proxy System
- `WorldProxyListener` supports NmsWorldProxy for dimension/world linking
- Prevents chunk unloads when a proxy world references those chunks

### Console Log Forwarding
- `ConsoleLogHandler` intercepts `java.util.logging`, strips ANSI colors, forwards to core via `Packet304ConsoleLog`
- Subscribe/unsubscribe via `Packet301Subscribe`/`Packet302Unsubscribe` for `"console.log"` event

### Game Queue Packets
- `Packet70QueueRegisterGame` — register game server/type with slot count
- `Packet71QueueRegisterPlayer` — enqueue a player
- `Packet72QueuedGameStart` — signal game start from queue
- `Packet73QueueUnregisterPlayer` — dequeue player
- `Packet74QueueInfo` — queue position and estimated time info

### MySQL Cache Packets
- `Packet75MysqlCacheRequest` — request cached row by key/table/columns
- `Packet76MysqlCacheResponse` — response with typed values
- `Packet77MysqlCacheUpdate` — push cache updates from core to bukkit

### New Command
- `/list` — shows online players with colored names (CHIEF+, or ORGANIZER+ during tournaments)
- `Packet11PlayerGiveExpSimple` for simplified exp granting

## 🔧 Changes

### Rank System Rework
- Package moved: `net.xtrafrancyz.Core.player.Rank` → `net.xtrafrancyz.Commons.player.Rank`
- New `ORGANIZER` rank inserted between DEV and MODER
- New `Permission` enum with BUILDER, PREFIX, VANISH, BAN, MUTE, ORGANIZER
- Ranks now have `EnumSet<Permission>` with static permission inheritance (e.g. CHIEF inherits MAPLEAD+WARDEN+ORGANIZER)
- `NetworkPlayer.has(Rank)` and `NetworkPlayer.has(Permission)` methods replace old `getRank().has()`

### Reports GUI Rework
- Completely rewritten from simple command to interactive 54-slot inventory GUI
- Shows violators sorted by report count with server name, report details, colored rank prefixes
- Actions: reject report, teleport to violator (cross-server via core), ban with predefined durations/reasons
- Auto-cleanup after 20 minutes of inactivity
- `/reports` now has `rs` alias

### Plugin Configuration & Registration
- `plugin.yml`: added guild/guildadm/list commands; `/reports` alias `rs`; website URL changed from `http` to `https`
- `PerWorldTablist` and `WorldProxyListener` registered as new event listeners
- `TeleportFix` listener **removed** (no longer registered)
- `ScheduledExecutorService` (`Executors.newSingleThreadScheduledExecutor()`) added for async scheduling
- Join teleport: vanish enabled with 1-tick delay, added confirmation message to player

### Chat Format
- Now supports guild tags: format changes from `player: message` to `<guild_tag> player: message` when applicable

## 📦 File Changes Summary

**New files** (net.xtrafrancyz package only):
- `commands/GuildCommand.java`, `commands/GuildAdminCommand.java`, `commands/ListCommand.java`
- `core/ConsoleLogHandler.java`, `core/BukkitPacketHandler.java`
- `listeners/PerWorldTablist.java`, `listeners/WorldProxyListener.java`
- `tasks/Restart.java`
- `impl/player/guild/VGuild.java`, `GuildMemberMenu.java`, `GuildLeaderMenu.java`, `GuildOfficerMenu.java`, `GuildPerksMenu.java`, `GuildColorMenu.java`
- `Commons/player/Permission.java`
- `Commons/guild/GuildStatus.java`, `GuildPerk.java`, `GuildLeveling.java`
- `Commons/season/GameSeason.java`
- `Core/network/packet/Packet69Guild.java`, `Packet70QueueRegisterGame.java`, `Packet71QueueRegisterPlayer.java`, `Packet72QueuedGameStart.java`, `Packet73QueueUnregisterPlayer.java`, `Packet74QueueInfo.java`, `Packet75MysqlCacheRequest.java`, `Packet76MysqlCacheResponse.java`, `Packet77MysqlCacheUpdate.java`

**Removed files**:
- `listeners/TeleportFix.java` (functionality removed)

**Moved files**:
- `Core/player/Rank.java` → `Commons/player/Rank.java` (restructured with Permission integration)
