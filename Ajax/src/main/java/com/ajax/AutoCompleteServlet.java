package com.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AutoCompleteServlet", urlPatterns = {"/autocomplete"})
public class AutoCompleteServlet extends HttpServlet {
    
    private ComposerData composerData;

    @Override
    public void init() throws ServletException {
        composerData = new ComposerData();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AutoCompleteServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AutoCompleteServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String targetId = request.getParameter("id");

        if (targetId != null) {
            targetId = targetId.trim().toLowerCase();
        } else {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        if ("complete".equals(action)) {
            StringBuilder sb = new StringBuilder();
            boolean namesAdded = false;

            if (!targetId.isEmpty()) {
                Iterator<Composer> iterator = composerData.getComposers().values().iterator();

                while (iterator.hasNext()) {
                    Composer composer = iterator.next();
                    String fullName = composer.getFirstName() + " " + composer.getLastName();

                    if (composer.getFirstName().toLowerCase().startsWith(targetId) ||
                        composer.getLastName().toLowerCase().startsWith(targetId) ||
                        fullName.toLowerCase().startsWith(targetId)) {

                        sb.append("<composer>");
                        sb.append("<id>").append(composer.getId()).append("</id>");
                        sb.append("<firstName>").append(composer.getFirstName()).append("</firstName>");
                        sb.append("<lastName>").append(composer.getLastName()).append("</lastName>");
                        sb.append("</composer>");
                        namesAdded = true;
                    }
                }
            }

            if (namesAdded) {
                response.setContentType("text/xml");
                response.setHeader("Cache-Control", "no-cache");
                response.getWriter().write("<composers>" + sb.toString() + "</composers>");
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } else if ("lookup".equals(action)) {
            if (targetId != null && composerData.getComposers().containsKey(targetId.trim())) {
                request.setAttribute("composer", composerData.getComposers().get(targetId));
                request.getRequestDispatcher("/composer.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Auto Complete Servlet";
    }
}
