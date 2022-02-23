package ScheduleGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Cell {
    public Rectangle2D.Double rect;
    public Color col;

    public String centerText;
    private float centerTextFs = 18F;
    private Point2D.Double centerTextLoc = null;
    public String underText;
    private float underTextFs = 18F;
    private Point2D.Double underTextLoc = null;

    public Cell(Rectangle2D.Double rect, Color col, String centerText, String underText) {
        this.rect = rect;
        this.col = col;
        this.centerText = centerText;
        this.underText = underText;
    }
    public Cell(Rectangle2D.Double rect, Color col) {
        this(rect, col, "", "");
    }
    public Cell(Rectangle2D.Double rect) {
        this(rect, Color.WHITE, "", "");
    }

    public void calcOnDrawRect(Rectangle2D.Double drawCell) {
        centerTextLoc = new Point2D.Double(
            drawCell.x + drawCell.width / 2 - (centerTextFs * centerText.length()) / 2,
            drawCell.y + drawCell.height / 2 + centerTextFs / 2
        );
        underTextLoc = new Point2D.Double(0,0);
        centerTextFs = 2;
        underTextFs = 2;
    }

    public float getCenterTextFs() {
        return centerTextFs;
    }

    public float getUnderTextFs() {
        return underTextFs;
    }

    public Point2D.Double getCenterTextLoc() {
        return centerTextLoc;
    }

    public Point2D.Double getUnderTextLoc() {
        return underTextLoc;
    }
}
