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

package me.duncte123.ghostbot.commands.space

import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.botcommons.web.WebUtils
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed

class ISSCommand extends Command {
    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        def data = WebUtils.ins.getJSONObject('http://api.open-notify.org/iss-now.json').execute()

        def position = data['iss_position']
        def latitude = position['latitude']
        def longitude = position['longitude']
        def mapsUrl = "https://google.com/maps/search/$latitude,$longitude"

        def embed = EmbedUtils.embedField('International Space Station',
            "The position of the ISS is [`$latitude`, `$longitude`]($mapsUrl)")

        sendEmbed(event, embed)
    }

    @Override
    String getName() { 'iss' }

    @Override
    String getHelp() { 'Shows the current location of the iss.' }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.SPACE
    }
}
