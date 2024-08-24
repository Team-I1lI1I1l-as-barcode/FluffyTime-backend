package com.fluffytime.bookmark.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class BookmarkAlreadyExists extends FluffyException {

    public BookmarkAlreadyExists() {
        super(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
    }
}
