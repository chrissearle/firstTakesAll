package no.anderska.wta.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.anderska.wta.SetupGame;
import no.anderska.wta.dto.AnswerLogEntryDTO;
import no.anderska.wta.dto.LogEntryDetailDTO;
import no.anderska.wta.logging.LogReader;

import com.google.gson.Gson;

public class LogReaderServlet extends HttpServlet {
    private LogReader logReader;

    @Override
    public void init() throws ServletException {
        logReader = SetupGame.instance().getLogReader();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        PrintWriter writer = resp.getWriter();

        Gson gson = new Gson();
        if ("/detail".equals(req.getPathInfo())) {
            String idstr = req.getParameter("id");
            if (idstr == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Must supply numeric id");
                return;
            }

            try {
                long id = Long.parseLong(idstr);
                List<LogEntryDetailDTO> detail = logReader.getDetail(id);
                if (detail == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Did not find entry with id " + id);
                    return;
                }
                writer.append(gson.toJson(detail));
                return;
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Must supply numeric id");
                return;
            }
        } else {
            List<AnswerLogEntryDTO> logEntries = logReader.getLogEntries();

            writer.append(gson.toJson(logEntries));
        }
    }
}