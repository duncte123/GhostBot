/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.duncte123.fandomapi.search;

import java.util.List;

public class LocalWikiSearchResultSet {

    private final int batches;
    private final List<LocalWikiSearchResult> items;
    private final int total;
    private final int currentBatch;
    private final int nextBatch;
    private final int next;

    public LocalWikiSearchResultSet(int batches, List<LocalWikiSearchResult> items, int total, int currentBatch, int next) {
        this.batches = batches;
        this.items = items;
        this.total = total;
        this.currentBatch = currentBatch;
        this.nextBatch = currentBatch + 1;
        this.next = next;
    }

    public int getBatches() {
        return batches;
    }

    public List<LocalWikiSearchResult> getItems() {
        return items;
    }

    public int getTotal() {
        return total;
    }

    public int getCurrentBatch() {
        return currentBatch;
    }

    public int getNextBatch() {
        return nextBatch;
    }

    public int getNext() {
        return next;
    }
}
