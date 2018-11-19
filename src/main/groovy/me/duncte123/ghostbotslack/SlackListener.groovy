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

package me.duncte123.ghostbotslack

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.variables.Variables

class SlackListener implements SlackMessagePostedListener {

    @Override
    void onEvent(SlackMessagePosted event, SlackSession session) {
        def user = event.user
        def content = event.messageContent.toLowerCase()

        if (user.bot) {
            return
        }

        if (!content.startsWith(Variables.PREFIX.toLowerCase())
            && !content.startsWith(Variables.OTHER_PREFIX.toLowerCase())) {
            return
        }

        SpoopyUtils.commandManager.handleCommand(event, session)

    }
}