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

package me.duncte123.ghostbot.objects.googlesearch

class GoogleSearchResults {

    public String kind
    public UrlField url
    public QueriesField queries
    public ContextField context
    public SearchInformationField searchInformation
    public List<SearchItem> items

    static class ContextField {
        public String title
    }

    static class ImageData {
        public String contextLink
        public int height
        public int width
        public int byteSize
        public String thumbnailLink
        public int thumbnailHeight
        public int thumbnailWidth
    }

    static class InnerQueries {
        public String title
        public String totalResults
        public String searchTerms
        public int count
        public int startIndex
        public String inputEncoding
        public String outputEncoding
        public String safe
        public String cx
    }

    static class QueriesField {
        public List<InnerQueries> request
        public List<InnerQueries> nextPage

    }

    static class SearchInformationField {
        public double searchTime
        public String formattedSearchTime
        public String totalResults
        public String formattedTotalResults
    }

    static class SearchItem {
        public String kind
        public String title
        public String htmlTitle
        public String link
        public String displayLink
        public String snippet
        public String htmlSnippet
        public String mime
        public ImageData image

    }

    static class UrlField {
        public String type
        public String template
    }
    
}
