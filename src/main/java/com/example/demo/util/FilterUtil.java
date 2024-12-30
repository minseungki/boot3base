package com.example.demo.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class FilterUtil {

    public boolean isFilterWhiteList(final String path) {
        return path.contains("/api/pass/pass-success")
                || path.contains("/api/file");
    }

    public String charEscape(String value) {
        return StringEscapeUtils.escapeHtml4(value);
    }

}
