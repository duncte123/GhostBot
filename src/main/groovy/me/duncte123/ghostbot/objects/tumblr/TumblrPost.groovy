/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostbot.objects.tumblr

class TumblrPost {

    String blog_name
    long id
    String post_url
    String short_url
    String type
    long timestamp
    String date
    String format
    String reblog_key
    String[] tags
    boolean bookmarklet
    boolean mobile
    String source_url
    String source_title
    boolean liked
    String state
    String total_posts

    /*Text posts*/
    String title
    String body

    /*Quopte posts*/
    String text
    String source

    /*Chat post*/
    List<TumblrDialogue> dialogue

    /*Photo post*/
    String caption
    String image_permalink
    int width
    int height
    List<TumblrPhoto> photos

    @Override
    boolean equals(Object obj) {

        if (!(obj instanceof TumblrPost))
            return false

        return this.id == ((TumblrPost) obj).id
    }

    @Override
    int hashCode() { String.valueOf(this.id).hashCode() }
}
