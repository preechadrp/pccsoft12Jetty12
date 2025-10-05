/* Mr.Preecha.d
 * เพื่อให้สามารถอ่าน HttpServletRequest  ได้มากกว่า 1 ครั้ง
 * ได้มาจาก https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-mvc-basics-3
 */
package com.pcc.api.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * ทำให้อ่าน Request ได้มากกว่า 1 ครั้ง
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = requestInputStream.readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // Create a reader from cachedContent
        // and return it
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}