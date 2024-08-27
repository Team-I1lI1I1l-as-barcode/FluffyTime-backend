package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.BookmarkErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class BookmarkNotFound extends FluffyException {

    public BookmarkNotFound() {
        super(BookmarkErrorCode.BOOKMARK_NOT_FOUND);
    }
}
