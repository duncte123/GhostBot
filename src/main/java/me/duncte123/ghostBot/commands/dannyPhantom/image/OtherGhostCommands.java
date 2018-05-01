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

package me.duncte123.ghostBot.commands.dannyPhantom.image;

import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

public class OtherGhostCommands extends ImageCommand {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        switch (invoke) {
            case "cujo":
                sendFromKeyword(event, "Cujo Danny Phantom");
                break;
            case "ember":
                sendFromKeyword(event, "ember mclain");
                break;
            case "dan":
                sendFromKeywords(event, "Dan Phantom", "Dark Danny");
                break;
            case "vlad":
                sendFromKeywords(event, "Vlad plasmius", "Vlad Masters");
                break;
            case "sam":
                sendFromKeyword(event, "Sam Manson");
                break;
            case "tucker":
                sendFromKeyword(event, "Tucker Foley");
                break;
            case "danny":
                sendFromKeywords(event, "Danny Fenton", "Danny Phantom");
                break;
            case "clockwork":
                sendFromKeyword(event, "Clockwork Danny Phantom");
                break;
            case "pitchpearl":
                sendFromKeyword(event, "pitch pearl");
                break;
        }
    }

    @Override
    public String getName() {
        return "cujo";
    }

    @Override
    public String getHelp() {
        return super.getHelp();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ember", "dan", "vlad", "sam", "tucker", "danny", "clockwork", "pitchpearl"};
    }

    private void sendFromKeywords(GuildMessageReceivedEvent event, String... words) {
        sendFromKeyword(
                event,
                words[SpoopyUtils.random.nextInt(words.length)]
        );
    }

    private void sendFromKeyword(GuildMessageReceivedEvent event, String keyword) {
        sendMsg(event, "Loading....", msg ->
                WebUtils.ins.getAson(SpoopyUtils.getGoogleSearchUrl(keyword)).async(
                        data -> sendMessageFromData(msg, data, keyword),
                        er -> sendMsg(event, "Error while looking up image: " + er)
                )
        );
    }
}
