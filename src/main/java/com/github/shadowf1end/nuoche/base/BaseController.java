package com.github.shadowf1end.nuoche.base;

import javax.servlet.http.HttpServletRequest;

/**
 * @author su
 */
public abstract class BaseController {

    protected Long getUserId(HttpServletRequest request) {
        return Long.valueOf(request.getAttribute("userId").toString());
    }
}
