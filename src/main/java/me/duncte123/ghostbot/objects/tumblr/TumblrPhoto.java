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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TumblrPhoto {

    private String caption;
    private TumblrPhotoElement original_size;
    private List<TumblrPhotoElement> alt_sizes;

    public void setOriginal_size(TumblrPhotoElement original_size) {
        this.original_size = original_size;
    }

    public void setOriginalsize(TumblrPhotoElement original_size) {
        this.original_size = original_size;
    }

    public void setAlt_sizes(List<TumblrPhotoElement> alt_sizes) {
        this.alt_sizes = alt_sizes;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public TumblrPhotoElement getOriginalSize() {
        return original_size;
    }

    public List<TumblrPhotoElement> getAltSizes() {
        return alt_sizes;
    }

    public void setAltSizes(List<TumblrPhotoElement> alt_sizes) {
        this.alt_sizes = alt_sizes;
    }

    public final static class TumblrPhotoElement {
        private String url;
        private int width;
        private int height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}
