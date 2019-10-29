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

package me.duncte123.ghostbot.utils;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.info.AudioTrackInfoBuilder;

import java.util.function.Function;

public class StoredTracks {

    public static final Function<YoutubeAudioSourceManager, YoutubeAudioTrack> REMEMBER = (manager) -> new YoutubeAudioTrack(AudioTrackInfoBuilder.empty()
        .setIdentifier("wBMOc24_aIw")
        .setAuthor("Ember McLain")
        .setTitle("Remember")
        .setLength(168000L)
        .setUri("https://www.youtube.com/watch?v=wBMOc24_aIw")
        .setIsStream(false)
        .build(), manager);

}
