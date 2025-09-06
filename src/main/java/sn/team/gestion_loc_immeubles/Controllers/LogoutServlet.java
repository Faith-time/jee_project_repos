package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Invalider la session
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Rediriger vers la page de connexion
        resp.sendRedirect(req.getContextPath() + "/auth/login");
    }
}
