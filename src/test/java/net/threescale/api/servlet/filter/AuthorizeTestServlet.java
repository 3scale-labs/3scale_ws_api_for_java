package net.threescale.api.servlet.filter;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class AuthorizeTestServlet extends GenericServlet {
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.setContentType("text/plain");
        PrintWriter writer = servletResponse.getWriter();
        writer.print("Test Servlet Called");
        writer.flush();
        writer.close();
    }
}
