/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Commons.season;

import java.time.LocalDate;
import java.time.ZoneId;

public abstract class GameSeason {
    private static final ZoneId UTC = ZoneId.of("UTC");
    public static final GameSeason MONTHLY = new MonthlyGameSeason();

    public abstract String getTableSuffix();

    public abstract boolean isEnding();

    private static class MonthlyGameSeason
    extends GameSeason {
        private long updated;
        private boolean ending;
        private String suffix;

        private MonthlyGameSeason() {
        }

        private void update() {
            if (System.currentTimeMillis() - this.updated > 60000L) {
                this.updated = System.currentTimeMillis();
                LocalDate now = LocalDate.now(UTC);
                this.suffix = "_monthly_" + (now.getMonthValue() % 2 == 0 ? "a" : "b");
                this.ending = now.getMonth().length(now.isLeapYear()) - now.getDayOfMonth() <= 2;
            }
        }

        @Override
        public String getTableSuffix() {
            this.update();
            return this.suffix;
        }

        @Override
        public boolean isEnding() {
            this.update();
            return this.ending;
        }
    }
}

