package ScheduleGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.min;

public class Cell {
    private static final float MAX_FONT_SIZE = 30;

    public Rectangle2D.Double rect;
    public Color col;

    public String centerText;
    private Font centerFont = null;
    private Point2D.Double centerTextLoc = null;
    private double centerTextWidth = 0;

    public String southText;
    private Font southFont = null;
    private Point2D.Double southTextLoc = null;
    private double southTextWidth = 0;

    private Point2D.Float textMargin;

    public Cell(Rectangle2D.Double rect, Color col, String centerText, String underText) {
        this.rect = rect;
        this.col = col;
        this.centerText = centerText;
        this.southText = underText;
    }
    public Cell(Rectangle2D.Double rect, Color col) {
        this(rect, col, "", "");
    }
    public Cell(Rectangle2D.Double rect) {
        this(rect, Color.WHITE, "", "");
    }

    public void calcFontsPosAndScale(Rectangle2D.Double drawCell, Graphics2D g2d, Point2D.Float margin) {
        textMargin = margin;

        if(southText != null) {
            String longestLine = getLongestLine(southText);
            float linesMul = southText.lines().count() * 1.2F;

            if(southFont == null)
                southFont = scaleFont(longestLine, (float) drawCell.width, g2d, margin.x);

            southTextWidth = g2d.getFontMetrics(southFont).stringWidth(longestLine);

            this.southTextLoc = new Point2D.Double(
                    drawCell.x + drawCell.width / 2 - southTextWidth / 2,
                    drawCell.y + drawCell.height
            );

            this.southTextLoc.y -= margin.y;
        }

        if(centerText != null) {
            String longestLine = getLongestLine(centerText);
            double linesMul = centerText.lines().count() * 1.2F;

            if (centerFont == null)
                centerFont = scaleFont(longestLine, (float) drawCell.width, g2d, margin.x);

            Rectangle2D.Double fakeDrawCell = drawCell;

            // move the center up
            if(southText != null) {
                double southH = g2d.getFontMetrics(southFont).getHeight() + margin.y;

                fakeDrawCell = new Rectangle2D.Double(
                        drawCell.x,
                        drawCell.y,
                        drawCell.width,
                        drawCell.height - southH
                );
            }

            centerTextWidth = g2d.getFontMetrics(centerFont).stringWidth(longestLine);
            double fontHeight = g2d.getFontMetrics(centerFont).getHeight();

            this.centerTextLoc = new Point2D.Double(
                    fakeDrawCell.x + fakeDrawCell.width / 2 - centerTextWidth / 2,
                    fakeDrawCell.y + fakeDrawCell.height / 2 - (fontHeight * linesMul) / 2
            );
        }
    }

    private Font scaleFont(String longestLine, float recWidth, Graphics2D g, float xMargin) {
        float fontSize = 20.0f;

        Font font = g.getFont().deriveFont(fontSize);
        int width = g.getFontMetrics(font).stringWidth(longestLine);
        fontSize = ((recWidth - xMargin) / width) * fontSize;

        return g.getFont().deriveFont(min(fontSize, MAX_FONT_SIZE));
    }

    public String getLongestLine(String text) {
        int longestLineLenght = 0, lastNewLinePos = 0;
        String longestLine = text;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                String line = text.substring(lastNewLinePos, i);
                if(longestLineLenght < line.length()) {
                    longestLineLenght = line.length();
                    longestLine = line;
                }
                lastNewLinePos = i;
            }
        }

        return longestLine;
    }

    public void setCenterFontSize(Font font, float fontSize) {
        this.centerFont = font.deriveFont(fontSize);
    }

    public void setSouthFontSize(Font font, float fontSize) {
        this.southFont = font.deriveFont(fontSize);
    }

    public Point2D.Double getCenterTextLoc() {
        return centerTextLoc;
    }

    public Point2D.Double getSouthTextLoc() {
        return southTextLoc;
    }

    public Font getCenterFont() {
        return centerFont;
    }

    public Font getSouthFont() {
        return southFont;
    }

    public Point2D.Float getTextMargin() {
        return textMargin;
    }

    public double getCenterTextWidth() {
        return centerTextWidth;
    }

    public double getSouthTextWidth() {
        return southTextWidth;
    }
}
