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

package me.duncte123.ghostBot.objects.googleSearch;

import java.util.List;

@SuppressWarnings("unused")
public class GoogleSearchResults {

    private String kind;
    private UrlField url;
    private QueriesField queries;
    private ContextField context;
    private SearchInformationField searchInformation;
    private List<SearchItem> items;

    public String getKind() {
        return kind;
    }

    public ContextField getContext() {
        return context;
    }

    public List<SearchItem> getItems() {
        return items;
    }

    public QueriesField getQueries() {
        return queries;
    }

    public SearchInformationField getSearchInformation() {
        return searchInformation;
    }

    public UrlField getUrl() {
        return url;
    }

    public static class ContextField {
        private String title;

        public ContextField() {
        }

        public String getTitle() {
            return title;
        }
    }

    public static class ImageData {
        private String contextLink;
        private int height;
        private int width;
        private int byteSize;
        private String thumbnailLink;
        private int thumbnailHeight;
        private int thumbnailWidth;

        public ImageData() {
        }

        public int getByteSize() {
            return byteSize;
        }

        public int getHeight() {
            return height;
        }

        public int getThumbnailHeight() {
            return thumbnailHeight;
        }

        public int getThumbnailWidth() {
            return thumbnailWidth;
        }

        public int getWidth() {
            return width;
        }

        public String getContextLink() {
            return contextLink;
        }

        public String getThumbnailLink() {
            return thumbnailLink;
        }
    }

    public static class InnerQueries {
        private String title;
        private String totalResults;
        private String searchTerms;
        private int count;
        private int startIndex;
        private String inputEncoding;
        private String outputEncoding;
        private String safe;
        private String cx;

        public InnerQueries() {
        }

        public int getCount() {
            return count;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public String getInputEncoding() {
            return inputEncoding;
        }

        public String getCx() {
            return cx;
        }

        public String getOutputEncoding() {
            return outputEncoding;
        }

        public String getSafe() {
            return safe;
        }

        public String getSearchTerms() {
            return searchTerms;
        }

        public String getTitle() {
            return title;
        }

        public String getTotalResults() {
            return totalResults;
        }
    }

    public static class QueriesField {
        private List<InnerQueries> request;
        private List<InnerQueries> nextPage;

        public QueriesField() {
        }

        public List<InnerQueries> getNextPage() {
            return nextPage;
        }

        public List<InnerQueries> getRequest() {
            return request;
        }

    }

    public static class SearchInformationField {
        private double searchTime;
        private String formattedSearchTime;
        private String totalResults;
        private String formattedTotalResults;

        public SearchInformationField() {
        }

        public String getTotalResults() {
            return totalResults;
        }

        public double getSearchTime() {
            return searchTime;
        }

        public String getFormattedSearchTime() {
            return formattedSearchTime;
        }

        public String getFormattedTotalResults() {
            return formattedTotalResults;
        }
    }

    public static class SearchItem {
        private String kind;
        private String title;
        private String htmlTitle;
        private String link;
        private String displayLink;
        private String snippet;
        private String htmlSnippet;
        private String mime;
        private ImageData image;

        public SearchItem() {
        }

        public String getTitle() {
            return title;
        }

        public String getDisplayLink() {
            return displayLink;
        }

        public String getHtmlSnippet() {
            return htmlSnippet;
        }

        public ImageData getImage() {
            return image;
        }

        public String getHtmlTitle() {
            return htmlTitle;
        }

        public String getKind() {
            return kind;
        }

        public String getLink() {
            return link;
        }

        public String getMime() {
            return mime;
        }

        public String getSnippet() {
            return snippet;
        }

    }

    public static class UrlField {
        private String type;
        private String template;

        public UrlField() {
        }

        public String getType() {
            return type;
        }

        public String getTemplate() {
            return template;
        }
    }
}
