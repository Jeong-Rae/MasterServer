package org.codequistify.master.stage.dto;

public record PageMetadata(
        int totalPages, int pageSize, int currentPageIndex, int currentPageElementCount, int totalElements) {
    public static PageMetadata of(
            int totalPages, int pageSize, int currentPageIndex, int currentPageElementCount, int totalElements) {
        return new PageMetadata(totalPages, pageSize, currentPageIndex, currentPageElementCount, totalElements);
    }
}
