package com.example.demo.filter;

import com.nhncorp.lucy.security.xss.XssFilter;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.example.demo.util.FilterUtil;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] b;

    public XssRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        XssFilter filter = XssFilter.getInstance("lucy-xss-sax.xml", true);
        b = filter.doFilter(getBody(request)).getBytes(StandardCharsets.UTF_8);
    }

    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(b);
        return new ServletInputStreamImpl(bis);
    }

    class ServletInputStreamImpl extends ServletInputStream {
        private final InputStream is;

        public ServletInputStreamImpl(InputStream bis) {
            is = bis;
        }

        public int read() throws IOException {
            return is.read();
        }

        public int read(byte[] b) throws IOException {
            return is.read(b);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) { }
    }

    public static String getBody(HttpServletRequest request) throws IOException {
        String body = null;
        BufferedReader br= null;
        StringBuilder sb= new StringBuilder();
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = br.read(charBuffer)) > 0) {
                    sb.append(charBuffer, 0, bytesRead);
                }

            } else {
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br!= null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        body = sb.toString();
        return body;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter); // 전달받은 parameter 불러오기

        if (values == null) {
            return null;
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                values[i] = cleanXSS(values[i]);
            }
        }

        return values;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null)
            return null;
        return cleanXSS(value);

    }

    private String cleanXSS(String value) {
        FilterUtil filterUtil = new FilterUtil();
        return filterUtil.charEscape(value);
    }

}