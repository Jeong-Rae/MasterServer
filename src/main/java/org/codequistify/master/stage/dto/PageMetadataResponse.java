package org.codequistify.master.stage.dto;

public record PageMetadataResponse(
        int totalPages, int pageSize, int currentPageIndex, int currentPageElementCount, int totalElements) {
    public static PageMetadataResponse of(
            int totalPages, int pageSize, int currentPageIndex, int currentPageElementCount, int totalElements) {
        return new PageMetadataResponse(totalPages, pageSize, currentPageIndex, currentPageElementCount, totalElements);
    }
}
