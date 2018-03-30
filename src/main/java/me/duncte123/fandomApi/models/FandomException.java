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

package me.duncte123.fandomApi.models;

public class FandomException implements FandomResult {

    private final String type;
    private final String message;
    private final int code;
    private final String details;

    private final String trace_id;

    public FandomException(String type, String message, int code, String details, String trace_id) {
        this.type = type;
        this.message = message;
        this.code = code;
        this.details = details;
        this.trace_id = trace_id;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }

    public String getMessage() {
        return message;
    }

    public String getTrace_id() {
        return trace_id;
    }

    @Override
    public String toString() {
        return getType() + ": " + getMessage() + "(" + getDetails() + ")";
    }
}
