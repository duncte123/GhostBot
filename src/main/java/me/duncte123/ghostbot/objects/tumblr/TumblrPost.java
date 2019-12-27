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
    public String summary;

    /*Text posts*/
    public String title;
    public String body;

    /*Quote posts*/
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

    public void setBlog_name(String blog_name) {
        this.blog_name = blog_name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPost_url(String post_url) {
        this.post_url = post_url;
    }

    public void setShort_url(String short_url) {
        this.short_url = short_url;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setReblog_key(String reblog_key) {
        this.reblog_key = reblog_key;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setBookmarklet(boolean bookmarklet) {
        this.bookmarklet = bookmarklet;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public void setSource_title(String source_title) {
        this.source_title = source_title;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTotal_posts(String total_posts) {
        this.total_posts = total_posts;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDialogue(List<TumblrDialogue> dialogue) {
        this.dialogue = dialogue;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setImage_permalink(String image_permalink) {
        this.image_permalink = image_permalink;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPhotos(List<TumblrPhoto> photos) {
        this.photos = photos;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof TumblrPost)) {
            return false;
        }

        return this.id == ((TumblrPost) obj).id;
    }

    @Override
    public int hashCode() {
        return String.valueOf(this.id).hashCode();
    }
}
