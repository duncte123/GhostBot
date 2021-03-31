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

package me.duncte123.ghostbot.commands.space;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;

public class ISSCommand extends Command {
    @Override
    public void execute(ICommandEvent event) {
        final ObjectNode data = WebUtils.ins.getJSONObject("http://api.open-notify.org/iss-now.json").execute();

        final JsonNode position = data.get("iss_position");

        final String latitude = position.get("latitude").asText();
        final String longitude = position.get("longitude").asText();
        final String mapsUrl = "https://google.com/maps/search/" + latitude + ',' + longitude;

        EmbedBuilder embed = EmbedUtils.embedField("International Space Station",
            String.format("The position of the ISS is [`%s`, `%s`](%s)", latitude, longitude, mapsUrl));

        sendEmbed(event, embed);
    }

    @Override
    public String getName() {
        return "iss";
    }

    @Override
    public String getHelp() {
        return "Shows the current location of the iss.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SPACE;
    }
}
