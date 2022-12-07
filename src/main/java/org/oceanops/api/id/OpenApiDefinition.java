package org.oceanops.api.id;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class
 */
public class OpenApiDefinition extends HttpServlet{

    public OpenApiDefinition(){
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/yaml");
        InputStream is = request.getServletContext().getResourceAsStream("/WEB-INF/classes/oceanops-apiid.yaml");
        BufferedInputStream bis = new BufferedInputStream(is);
        ServletOutputStream stream = response.getOutputStream();
        stream.write(bis.readAllBytes());

        stream.close();
        bis.close();
    }
}
