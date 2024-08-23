package com.fluffytime.bookmark.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class BookmarkNotFound extends FluffyException {

    public BookmarkNotFound() {
        super(BookmarkErrorCode.BOOKMARK_NOT_FOUND);
    }
}
