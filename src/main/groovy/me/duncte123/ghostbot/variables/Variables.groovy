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

package me.duncte123.ghostbot.variables

import me.duncte123.ghostbot.utils.SpoopyUtils

class Variables {

    public static final String PREFIX = SpoopyUtils.config.discord.prefix
    public static final String OTHER_PREFIX = 'gb!'
    public static final long OWNER_ID = 191231307290771456L
    public static final String VERSION = '@ghostBotVersion@'
    public static final String FOOTER_ICON = 'https://cdn.discordapp.com/emojis/394148311835344896.png'
    public static final int EMBED_COLOR = 0x6ffe32
    public static final String EMBED_COLOR_SLACK = '#6ffe32'
    public static final String GHOSTBOT_GUILD = 'https://discord.gg/NKM9Xtk'
    public static final String GHOSTBOT_INVITE = 'https://discordapp.com/oauth2/authorize?client_id=397297702150602752&scope=bot&permissions=36817984'

}
