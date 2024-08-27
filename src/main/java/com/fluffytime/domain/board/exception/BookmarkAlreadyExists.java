package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.BookmarkErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class BookmarkAlreadyExists extends FluffyException {

    public BookmarkAlreadyExists() {
        super(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
    }
}
