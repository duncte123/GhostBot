/*
 * GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018  Duncan "duncte123" Sterken
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

package me.duncte123.ghostBot.objects.googleSearch;

public class ImageData {
    private String contextLink;
    private int height;
    private int width;
    private int byteSize;
    private String thumbnailLink;
    private int thumbnailHeight;
    private int thumbnailWidth;

    public ImageData() {}

    public int getByteSize() {
        return byteSize;
    }

    public int getHeight() {
        return height;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public int getWidth() {
        return width;
    }

    public String getContextLink() {
        return contextLink;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }
}
