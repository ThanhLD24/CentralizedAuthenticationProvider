package com.esoft.web.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.*;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedContent = new ByteArrayOutputStream();
    private final ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedBodyHttpServletResponse(HttpServletResponse response) throws IOException {
        super(response);
        this.outputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // Not needed
            }

            @Override
            public void write(int b) {
                cachedContent.write(b);
            }
        };
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(cachedContent, getCharacterEncoding()), true);
        }
        return writer;
    }

    public byte[] getCachedBody() {
        return cachedContent.toByteArray();
    }

    public void copyBodyToResponse() throws IOException {
        ServletOutputStream responseOutputStream = getResponse().getOutputStream();
        responseOutputStream.write(getCachedBody());
        responseOutputStream.flush();
    }
}
