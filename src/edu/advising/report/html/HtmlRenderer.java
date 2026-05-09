package edu.advising.report.html;

import edu.advising.report.ReportRenderer;

public class HtmlRenderer implements ReportRenderer {
    @Override
    public String render(String title, String body) {
        return "<!DOCTYPE html>\n<html>\n<head><title>" + title + "</title></head>\n" +
               "<body>\n" + body + "\n</body>\n</html>";
    }
}
