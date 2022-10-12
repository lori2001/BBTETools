package ScheduleGenerator.graphics;

import ScheduleGenerator.data.SGData;
import ScheduleGenerator.utils.StringProcessor;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;

import static java.lang.Math.min;

public class DrawableText {
    private String text = null;
    private Font font = null;
    private Point2D.Double pos = new Point2D.Double(0, 0);
    private Point2D.Double size = new Point2D.Double(0, 0);
    private double lineGap = 0; // space between lines of text
    private boolean fontSizeIsSet = false;

    public DrawableText(){}
    public DrawableText(String text, Font font, Point2D.Double pos, Point2D.Double size, double lineGap) {
        setText(text);
        this.font = font;
        setPos(pos);
        setLineGap(lineGap);
    }

    public void scaleFont(double recWidth, Graphics2D g2d, float maxFontSize) {
        float fontSize = 20.0f;

        String longestLine = StringProcessor.getLongestLine(text);
        Font f = g2d.getFont().deriveFont(fontSize);
        int width = g2d.getFontMetrics(f).stringWidth(longestLine);
        fontSize = (float) ((recWidth / width) * fontSize);

        this.font = g2d.getFont().deriveFont(min(fontSize, maxFontSize));
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setFontSize(Font font, float fontSize) {
        this.font = font.deriveFont(fontSize);
        fontSizeIsSet = true;
    }

    public boolean isFontSizeSet() {
        return fontSizeIsSet;
    }

    public void setPos(Point2D.Double pos) {
        this.pos = pos;
    }

    public void setLineGap(double lineGap) {
        this.lineGap = lineGap;
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public void calcSize(Graphics2D g2d)
    {
        size = calcSizeOf(text, font, lineGap, g2d);
    }

    public boolean shouldBeDrawn() {
        return text != null && font != null && pos != null && size != null;
    }

    public Point2D.Double getSize() {
        return size;
    }

    public Point2D.Double getPos() {
        return pos;
    }

    public double getLineGap() {
        return lineGap;
    }

    static public Point2D.Double calcSizeOf(String text, Font font, double lineGap, Graphics2D g2d)
    {
        double height = 0;

        int nrOfLines = (int) text.lines().count();
        height += lineGap * nrOfLines;

        for(String line : text.lines().toList()) {
            FontRenderContext frcH = g2d.getFontRenderContext();
            GlyphVector gvH = font.createGlyphVector(frcH, line);
            height += gvH.getPixelBounds(null, 0, 0).height;
        }

        // width
        String longestLine = StringProcessor.getLongestLine(text);
        double width = g2d.getFontMetrics(font).stringWidth(longestLine);

        return new Point2D.Double(width, height);
    }

    public void draw(Graphics2D g2d)  {
        Point2D.Double cLinePos = new Point2D.Double (pos.x + size.x / 2, pos.y);

        g2d.setFont(font);
        g2d.setColor(SGData.Colors.FONT_COLOR);

        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            Point2D.Double size = DrawableText.calcSizeOf(lines[i], font, lineGap, g2d);
            Point2D.Double pos = new Point2D.Double(
                    cLinePos.x - size.x / 2,
                    cLinePos.y + size.y * (i + 1) - lineGap / 2
            );
            g2d.drawString(lines[i], (int) pos.x, (int) pos.y);
        }
    }
}
