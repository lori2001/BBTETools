package ScheduleGenerator;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Cell {
    private final float maxFontSize;

    public Rectangle rect;
    public Color col;

    private final String centerText;
    private Font centerFont = null;
    private Point2D.Double centerTextLoc = null;
    private Point2D.Double centerTextSize = null;

    private final String southText;
    private Font southFont = null;
    private Point2D.Double southTextLoc = null;
    private Point2D.Double southTextSize = null;

    private final String topLeftText;
    private Font topLeftFont = null;
    private Point2D.Double topLeftTextLoc = null;
    private Point2D.Double topLeftTextSize = null;

    private final double lineGap;

    public Cell(Rectangle rect, Color col, String centerText, String underText, String topLeftText,
                Point2D.Double scale, double lineGap)
    {
        maxFontSize = (float) (7.5f * scale.x);
        this.lineGap = lineGap;

        this.rect = rect;
        this.col = col;
        this.centerText = centerText;
        this.southText = underText;
        this.topLeftText = topLeftText;
    }

    public void calcTextsPosAndScale(Rectangle2D.Double drawCell, Graphics2D g2d, Point2D.Double margin) {
        if(southText != null) {
            if(southFont == null)
                southFont = scaleFont(southText, (float) drawCell.width, g2d, margin.x);

            southTextSize = getTextSize(g2d, southFont, southText, 0);

            this.southTextLoc = new Point2D.Double(
                    drawCell.x + drawCell.width / 2 - southTextSize.x / 2,
                    drawCell.y + drawCell.height - margin.y
            );
        }

        // this needs no "auto scale" !!
        if(topLeftText != null && topLeftFont != null) {
            topLeftTextSize = getTextSize(g2d, topLeftFont, topLeftText, 0);

            this.topLeftTextLoc = new Point2D.Double(
                    drawCell.x + margin.x,
                    drawCell.y + topLeftTextSize.y + margin.y
            );
        }

        if(centerText != null) {
            String longestLine = getLongestLine(centerText);

            if (centerFont == null){
                centerFont = scaleFont(longestLine, (float) drawCell.width, g2d, margin.x);
            }

            centerTextSize = getTextSize(g2d, centerFont, centerText, lineGap);

            Rectangle2D.Double adjustDrawCell =
                    new Rectangle2D.Double(drawCell.x, drawCell.y, drawCell.width, drawCell.height);

            // move the center up if only south text
            if(southText != null && topLeftText == null) {
                adjustDrawCell.height -= southTextSize.y;
            }
            // move the center down if only north text
            else if(southText == null && topLeftText != null) {
                adjustDrawCell.y += topLeftTextSize.y;
                adjustDrawCell.height -= topLeftTextSize.y;
            }

            this.centerTextLoc = new Point2D.Double(
                    adjustDrawCell.x + drawCell.width / 2  - centerTextSize.x / 2,
                    adjustDrawCell.y + drawCell.height / 2 - centerTextSize.y / 2
            );
        }
    }

    private Font scaleFont(String longestLine, double recWidth, Graphics2D g, double xMargin) {
        float fontSize = 20.0f;

        Font font = g.getFont().deriveFont(fontSize);
        int width = g.getFontMetrics(font).stringWidth(longestLine);
        fontSize = (float) (((recWidth - xMargin * 2) / width) * fontSize);

        return g.getFont().deriveFont(min(fontSize, maxFontSize));
    }

    public static String getLongestLine(String text) {
        int longestLineLenght = 0;
        String longestLine = text;

        String[] lines = text.split("\n");
        for (String line: lines) {
            if(line.length() > longestLineLenght) {
                longestLineLenght = line.length();
                longestLine = line;
            }
        }

        return longestLine;
    }

    public static Point2D.Double getTextSize(Graphics2D g2, Font font, String text, double lineGap)
    {
        // TODO: this is a bit of a hack, change if you can come up w something better
        double height = 0;
        int nrOfLines = (int) text.lines().count();
        if(nrOfLines == 1)
            height += lineGap * nrOfLines;
        else
            height += lineGap * (nrOfLines - 1);

        for(String line : text.lines().toList()) {
            FontRenderContext frcH = g2.getFontRenderContext();
            GlyphVector gvH = font.createGlyphVector(frcH, line);
            height += gvH.getPixelBounds(null, 0, 0).height;
        }

        // width
        String longestLine = getLongestLine(text);
        double width = g2.getFontMetrics(font).stringWidth(longestLine);

        return new Point2D.Double(width, height);
    }

    public void setCenterFontSize(Font font, float fontSize) {
        this.centerFont = font.deriveFont(fontSize);
    }

    public void setSouthFontSize(Font font, float fontSize) {
        this.southFont = font.deriveFont(fontSize);
    }

    public void setTopLeftFontSize(Font font, float fontSize) {
        this.topLeftFont = font.deriveFont(fontSize);
    }

    public Point2D.Double getCenterTextLoc() {
        return centerTextLoc;
    }

    public Point2D.Double getSouthTextLoc() {
        return southTextLoc;
    }

    public Point2D.Double getTopLeftTextLoc() {
        return topLeftTextLoc;
    }

    public Font getCenterFont() {
        return centerFont;
    }

    public Font getSouthFont() {
        return southFont;
    }

    public Font getTopLeftFont() {
        return topLeftFont;
    }

    public Point2D.Double getCenterTextSize() {
        return centerTextSize;
    }

    public Point2D.Double getSouthTextSize() {
        return southTextSize;
    }

    public Point2D.Double getTopLeftTextSize() {
        return topLeftTextSize;
    }

    public String getCenterText() {
        return centerText;
    }

    public String getSouthText() {
        return southText;
    }

    public String getTopLeftText() {
        return topLeftText;
    }

    public double getLineGap() {
        return lineGap;
    }
}
