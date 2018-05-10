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
 *     but WITHOUT ANY WARRANTY without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.duncte123.ghostBot.objects.googleSearch

@SuppressWarnings("GroovyUnusedDeclaration")
class GoogleSearchResults {

    String kind
    UrlField url
    QueriesField queries
    ContextField context
    SearchInformationField searchInformation
    List<SearchItem> items

    static class ContextField {
        String title
    }

    static class ImageData {
        String contextLink
        int height
        int width
        int byteSize
        String thumbnailLink
        int thumbnailHeight
        int thumbnailWidth
    }

    static class InnerQueries {
        String title
        String totalResults
        String searchTerms
        int count
        int startIndex
        String inputEncoding
        String outputEncoding
        String safe
        String cx
    }

    static class QueriesField {
        List<InnerQueries> request
        List<InnerQueries> nextPage

    }

    static class SearchInformationField {
        double searchTime
        String formattedSearchTime
        String totalResults
        String formattedTotalResults
    }

    static class SearchItem {
        String kind
        String title
        String htmlTitle
        String link
        String displayLink
        String snippet
        String htmlSnippet
        String mime
        ImageData image

    }

    static class UrlField {
        String type
        String template
    }
}
