/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.event.Event
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerTagChangeEvent;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

public class PlayerTag {
    private static final Pattern CLEANUP_SPACES_REGEX = Pattern.compile("( )\\1+");
    private static final int MAX_TAG_LENGTH = 36;
    private static final TagComponent SPACE = new TextComponent(" ");
    private static final TagComponent GUILD_TAG = new GuildTagComponent();
    private static final TagComponent GUILD_COLOR = new GuildColorComponent();
    private static final TagComponent RANK_COLOR = new RankColorComponent();
    private static final TagComponent RANK_PREFIX = new RankPrefixComponent();
    private final List<TagComponent> prefix;
    private final List<TagComponent> suffix;
    private final VPlayer player;
    private String username;
    private String compiled;

    public PlayerTag(VPlayer player) {
        this.player = player;
        this.prefix = new ArrayList<TagComponent>();
        this.suffix = new ArrayList<TagComponent>();
        this.compiled = player.username;
        this.username = player.username;
        this.setDefaultFormat();
    }

    public String getVisibleName() {
        return this.compiled;
    }

    public void setName(String name) {
        this.username = name;
        this.refresh();
    }

    public boolean isModified() {
        return !this.player.username.equals(this.compiled);
    }

    public TagBuilder newPrefix() {
        this.prefix.clear();
        return new TagBuilder(this, this.prefix);
    }

    public TagBuilder newSuffix() {
        this.suffix.clear();
        return new TagBuilder(this, this.suffix);
    }

    public void refresh() {
        String old = this.compiled;
        this.compiled = this.compileList(this.prefix) + this.username + this.compileList(this.suffix);
        this.compiled = CLEANUP_SPACES_REGEX.matcher(this.compiled.trim()).replaceAll("$1");
        if (this.compiled.length() > 36) {
            this.compiled = this.compiled.substring(0, 36);
        }
        if (this.compiled.equals(old)) {
            return;
        }
        Bukkit.getPluginManager().callEvent((Event)new PlayerTagChangeEvent(this.player, old));
    }

    public void reset() {
        this.setDefaultFormat();
        this.refresh();
    }

    private void setDefaultFormat() {
        if (!VimeNetwork.features().CHANGE_TAGS.isEnabled()) {
            this.prefix.clear();
            this.suffix.clear();
        } else {
            TagBuilder builder = this.newPrefix();
            if (VimeNetwork.features().ADD_GUILD_TAGS.isEnabled()) {
                builder.guildTag().space();
            }
            builder.rankColor();
        }
    }

    private String compileList(List<TagComponent> list) {
        StringBuilder sb = new StringBuilder(16);
        for (TagComponent component : list) {
            sb.append(component.getText(this.player));
        }
        return U.normalizeColors(U.colored(sb.toString()));
    }

    private static class RankColorComponent
    implements TagComponent {
        private RankColorComponent() {
        }

        @Override
        public String getText(VPlayer player) {
            return player.getRank().getColor();
        }
    }

    private static class RankPrefixComponent
    implements TagComponent {
        private RankPrefixComponent() {
        }

        @Override
        public String getText(VPlayer player) {
            String prefix = player.getRankPrefix();
            if (prefix.isEmpty()) {
                return "";
            }
            return "[" + prefix + "]";
        }
    }

    private static class GuildColorComponent
    implements TagComponent {
        private GuildColorComponent() {
        }

        @Override
        public String getText(VPlayer player) {
            return player.guild == null ? "" : player.guild.color;
        }
    }

    private static class GuildTagComponent
    implements TagComponent {
        private GuildTagComponent() {
        }

        @Override
        public String getText(VPlayer player) {
            if (player.guild == null || player.guild.tag == null) {
                return "";
            }
            return "&7<" + player.guild.color + player.guild.tag + "&7>&r";
        }
    }

    private static class TextComponent
    implements TagComponent {
        private String text;

        public TextComponent(String text) {
            this.text = text;
        }

        @Override
        public String getText(VPlayer player) {
            return this.text;
        }
    }

    private static interface TagComponent {
        public String getText(VPlayer var1);
    }

    public static class TagBuilder {
        private PlayerTag tag;
        private List<TagComponent> list;

        private TagBuilder(PlayerTag tag, List<TagComponent> list) {
            this.tag = tag;
            this.list = list;
        }

        public TagBuilder text(String text) {
            this.list.add(new TextComponent(text));
            return this;
        }

        public TagBuilder color(ChatColor color) {
            this.list.add(new TextComponent(color.toString()));
            return this;
        }

        public TagBuilder guildTag() {
            this.list.add(GUILD_TAG);
            return this;
        }

        public TagBuilder guildColor() {
            this.list.add(GUILD_COLOR);
            return this;
        }

        public TagBuilder space() {
            this.list.add(SPACE);
            return this;
        }

        public TagBuilder rankColor() {
            this.list.add(RANK_COLOR);
            return this;
        }

        public TagBuilder rankPrefix() {
            this.list.add(RANK_PREFIX);
            return this;
        }

        public void save() {
            this.tag.refresh();
        }
    }
}

