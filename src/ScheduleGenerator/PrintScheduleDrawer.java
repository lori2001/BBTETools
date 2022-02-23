package ScheduleGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PrintScheduleDrawer {
    Point2D.Double A4InMM = new Point2D.Double(297, 210);
    Point2D.Double border = new Point2D.Double(5, 5); // mm
    Point2D.Double pos, size, scale;

    Graphics2D g2d = null;

    Font font;
    Color fontColor = new Color(218,218,218);

    Color baseColor = new Color(57,57,57);

    // DATA
    int cols = 15; // 1 + 14
    int rows = 11; // 1 + (5 * 2)

    Color[] colors = new Color[]{
            new Color(255,2,255),
            new Color(0,255, 0),
            new Color(255,255,0),
    };

    String[] DOW_HU = { "Hé" , "Ke", "Sze", "Csü", "Pé" };

    PrintScheduleDrawer(Point2D.Double pos, Point2D.Double scale) {
        this.scale = scale;

        this.pos = pos;
        this.size = new Point2D.Double(scale.x * A4InMM.x, scale.y * A4InMM.y);

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Eras-Bold-ITC.ttf"));
        } catch (IOException|FontFormatException e) {
            e.printStackTrace();
        }
    }

    public void paintComponents(Graphics g) {
        g2d = (Graphics2D) g;

        // background
        g2d.setColor(Color.WHITE);
        g2d.fill(new Rectangle2D.Double(pos.x, pos.y, size.x, size.y));

        System.out.println(pos.x);

        drawTable(new Point2D.Double(0,0), new Point2D.Double(A4InMM.x, A4InMM.y));

        System.out.println(pos.x);

    }

    /*private void drawHeader(Point2D.Double tPos, Point2D.Double tSizeMM) {
        Point2D.Double sizeInMM = new Point2D.Double(23, 200);
        int cols = 13;

        for(double x = 0; x <= tSize.x - xOffs; x += xOffs) {
            col = 0;
            for(double y = 0; y <= tSize.y - yOffs; y += yOffs) {
                drawRect(absPos.x + x + clsBorder.x, absPos.y + y + clsBorder.y,
                        tSize.x / cols - clsBorder.y, tSize.y / rows - clsBorder.y,
                        possibleColors[i % 5]);
                col++;
                i++;
            }
            row++;
            i++;
        }
    }*/

    private void drawTable(Point2D.Double tPos, Point2D.Double tSize) {
        Point2D.Double absPos = new Point2D.Double(pos.x, pos.y); // , tSize = new Vec(size.x, size.y);

        // convert MM to px
        tPos.x *= scale.x;
        tPos.y *= scale.y;
        tSize.x *= scale.x;
        tSize.y *= scale.y;

        tSize.x -= border.x * scale.x * 2; // left, right
        tSize.y -= border.y * scale.x * 2; // top, bottom
        absPos.x += border.x * scale.x + tPos.x;
        absPos.y += border.y * scale.y + tPos.y;

        Point2D.Double clsBorder = new Point2D.Double(1 * scale.x,1  * scale.y); // mm

        int xI = 0, yI = 0, i = 0;
        double xOffs = tSize.x / cols;
        double yOffs = tSize.y / rows;

        ArrayList<Cell> cells = generateCells();

        for(double x = 0; x <= tSize.x - xOffs + 1; x += xOffs) {
            yI = 0;

            for(double y = 0; y <= tSize.y - yOffs + 1; y += yOffs) {

                Rectangle2D.Double drawCell = new Rectangle2D.Double(
                        absPos.x + x + clsBorder.x, absPos.y + y + clsBorder.y,
                        xOffs - clsBorder.y, yOffs - clsBorder.y);

                boolean inAnyCell = false;
                for(Cell cell : cells) {
                    if(cell.rect.x == xI && cell.rect.y == yI) {
                        drawCell.width += (cell.rect.width - 1) * xOffs;
                        drawCell.height += (cell.rect.height - 1) * yOffs;
                        cell.calcOnDrawRect(drawCell);
                        drawRect(drawCell, cell);
                    }

                    if(!inAnyCell)
                        inAnyCell = !(xI >= cell.rect.x + cell.rect.width) && !(yI >= cell.rect.y + cell.rect.height)
                                     && !(xI < cell.rect.x) && !(yI < cell.rect.y);
                }

                if(!inAnyCell) {
                    if(xI == 0 || yI == 0) {
                       drawRect(drawCell, baseColor);
                    } else {
                       drawRect(drawCell, Color.black);
                    }
                }

                yI++;
                i++;
            }
            xI++;
            i++;
        }
    }

    private void drawRect(Rectangle2D.Double rect, Color color) {
        g2d.setColor(color);
        g2d.fill(rect);
    }

    private void drawRect(Rectangle2D.Double rect, Cell cell) {
        g2d.setColor(cell.col);
        g2d.fill(rect);

        // Define rendering hint, font name, font style and font size
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(font.deriveFont(18F));
        g2d.setPaint(fontColor);

        g2d.drawString(cell.centerText, (int)cell.getCenterTextLoc().x, (int)cell.getCenterTextLoc().y);
    }

    private ArrayList<Cell> generateCells() {
        ArrayList<Cell> cells = new ArrayList<>();

        // days
        for(int y = 1; y < rows; y+= 2) {
            Rectangle2D.Double t = new Rectangle2D.Double(0, y, 1, 2);
            cells.add(new Cell(t, baseColor, "Hé", "Szem"));
        }

        return cells;
    }




}
