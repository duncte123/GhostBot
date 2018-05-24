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

package me.duncte123.ghostBot.objects.tumblr;

import java.util.List;

@SuppressWarnings("unused")
public class TumblrPost {

    public String blog_name;
    public long id;
    public String post_url;
    public String short_url;
    public String type;
    public long timestamp;
    public String date;
    public String format;
    public String reblog_key;
    public String[] tags;
    public boolean bookmarklet;
    public boolean mobile;
    public String source_url;
    public String source_title;
    public boolean liked;
    public String state;
    public String total_posts;

    /*Text posts*/
    public String title;
    public String body;

    /*Quopte posts*/
    public String text;
    public String source;

    /*Chat post*/
    public List<TumblrDialogue> dialogue;

    /*Photo post*/
    public String caption;
    public String image_permalink;
    public int width;
    public int height;
    public List<TumblrPhoto> photos;


    public TumblrPost() {
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  TumblrPost))
            return false;

        return this.id == ((TumblrPost) obj).id;
    }
}