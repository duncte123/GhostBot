/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2019  Duncan "duncte123" Sterken
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

package me.duncte123.ghostbot.objects.tumblr;

import java.util.List;

public class TumblrPhoto {

    private final String caption;
    private final TumblrPhotoElement original_size;
    private final List<TumblrPhotoElement> alt_sizes;

    public TumblrPhoto(String caption, TumblrPhotoElement original_size, List<TumblrPhotoElement> alt_sizes) {
        this.caption = caption;
        this.original_size = original_size;
        this.alt_sizes = alt_sizes;
    }

    public String getCaption() {
        return caption;
    }

    public TumblrPhotoElement getOriginalSize() {
        return original_size;
    }

    public List<TumblrPhotoElement> getAltSizes() {
        return alt_sizes;
    }

    public final static class TumblrPhotoElement {
        private final String url;
        private final int width;
        private final int height;

        public TumblrPhotoElement(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

}
