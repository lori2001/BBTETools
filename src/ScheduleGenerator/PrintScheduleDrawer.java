package ScheduleGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class PrintScheduleDrawer {
    Point2D.Double A4InMM = new Point2D.Double(297, 210);
    Point2D.Double border = new Point2D.Double(3, 3); // mm
    Point2D.Double pos, size, scale;

    Graphics2D g2d = null;
    Font font = null;

    Color BACKGROUND_COLOR = new Color(211, 211, 211);
    Color FONT_COLOR = new Color(218,218,218);
    Color BASE_COLOR = new Color(57,57,57);
    Color SOUTH_BG_COLOR = new Color(0,0,0, 178);
    Color[] CLASS_COLORS = new Color[]{
            new Color(0,166,81),
            new Color(46,49, 146),
            new Color(237,28,36),
            new Color(0,174, 239),
            new Color(102,45,145),
            new Color(233,127,36),
            new Color(236,0,140),
            new Color(122,204,200),
            new Color(253,198,137),
            new Color(117,76,36),
    };

    int cols; // 15 = 1 + 14
    int rows; // 11 = 1 + (5 * 2)
    String[] dowHu = { "Hé", "Ke", "Sze", "Csü", "Pé" };
    ArrayList<LocalTime[]> intervals;
    String topLeftCont = "M-I\n1.1";

    PrintScheduleDrawer(Point2D.Double pos, Point2D.Double scale, ArrayList<LocalTime[]> intervals) {
        this.scale = scale;

        this.pos = pos;
        this.size = new Point2D.Double(scale.x * A4InMM.x, scale.y * A4InMM.y);

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Eras-Bold-ITC.ttf")).deriveFont(10F);
        } catch (IOException|FontFormatException e) {
            e.printStackTrace();
        }

        if(intervals != null)
            this.intervals = intervals;

        cols = 1 + this.intervals.size(); // header + num of hours
        rows = 1 + this.dowHu.length * 2; // side + dow * 2
    }

    public void paintComponents(Graphics g, ArrayList<Course> courses) {
        g2d = (Graphics2D) g;
        g2d.setFont(font);

        // background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fill(new Rectangle2D.Double(pos.x, pos.y, size.x, size.y));

        ArrayList<Cell> cells = generateCells(courses);

        drawTable(new Point2D.Double(0,0), new Point2D.Double(A4InMM.x, A4InMM.y), cells);
    }

    private void drawTable(Point2D.Double tPos, Point2D.Double tSize, ArrayList<Cell> cells) {
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

        int xI = 0, yI, i = 0;
        double xOffs = tSize.x / cols;
        double yOffs = tSize.y / rows;

        for(double x = 0; x <= tSize.x - xOffs + 1; x += xOffs) {
            yI = 0;
            for(double y = 0; y <= tSize.y - yOffs + 1; y += yOffs) {

                Rectangle2D.Double cellCoords = new Rectangle2D.Double(
                        absPos.x + x + clsBorder.x, absPos.y + y + clsBorder.y,
                        xOffs - clsBorder.y, yOffs - clsBorder.y);

                boolean inAnyCell = false;
                for(Cell cell : cells) {
                    if(cell.rect.x == xI && cell.rect.y == yI) {
                        cellCoords.width += (cell.rect.width - 1) * xOffs;
                        cellCoords.height += (cell.rect.height - 1) * yOffs;

                        cell.calcFontsPosAndScale(cellCoords, g2d, new Point2D.Float(10, 5));

                        drawCell(cellCoords, cell);
                    }

                    if(!inAnyCell)
                        inAnyCell = !(xI >= cell.rect.x + cell.rect.width) && !(yI >= cell.rect.y + cell.rect.height)
                                     && !(xI < cell.rect.x) && !(yI < cell.rect.y);
                }

                if(!inAnyCell) {
                    //if(xI == 0 || yI == 0) {
                       //drawRect(drawCell, baseColor);
                    //} else {
                        drawRect(cellCoords, Color.WHITE);
                    //}
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

    private void drawCell(Rectangle2D.Double rect, Cell cell) {
        drawRect(rect, cell.col);

        // Define rendering hint, font name, font style and font size
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(FONT_COLOR);

        if(cell.getCenterFont() != null) {
            g2d.setFont(cell.getCenterFont());
            int lineHeight = g2d.getFontMetrics().getHeight();

            int x = (int) (cell.getCenterTextLoc().x + cell.getCenterTextWidth() / 2);
            int y = (int) cell.getCenterTextLoc().y;

            for (String line : cell.centerText.split("\n")) {
                int width = g2d.getFontMetrics(cell.getCenterFont()).stringWidth(line);
                g2d.drawString(line, x - width / 2, y += lineHeight);
            }
        }

        if(cell.getSouthFont() != null) {
            g2d.setFont(cell.getSouthFont());

            float southBgHeight = g2d.getFontMetrics().getHeight() + cell.getTextMargin().y;
            Rectangle2D.Double southBg = new Rectangle2D.Double(
                    rect.x, rect.y + rect.height - southBgHeight,
                    rect.width, southBgHeight
            );
            drawRect(southBg, SOUTH_BG_COLOR);

            g2d.setPaint(FONT_COLOR);
            g2d.drawString(cell.southText, (int)cell.getSouthTextLoc().x, (int)cell.getSouthTextLoc().y);
        }
    }

    private ArrayList<Cell> generateCells(ArrayList<Course> courses) {
        ArrayList<Cell> cells = new ArrayList<>();

        // top left content
        Cell topLeftCell = new Cell(new Rectangle2D.Double(0, 0, 1, 1), BASE_COLOR, topLeftCont, null);
        topLeftCell.setCenterFontSize(font, (float) (this.scale.x * 7F));
        cells.add(topLeftCell);

        // days
        for(int i = 1; i <= dowHu.length; i++) {
            Rectangle2D.Double t = new Rectangle2D.Double(0, i * 2 - 1, 1, 2);

            Cell cell = new Cell(t, BASE_COLOR, dowHu[i - 1], null);
            cell.setCenterFontSize(font, (float) (this.scale.x * 9F));

            cells.add(cell);
        }

        // intervals
        for(int i = 1; i <= intervals.size(); i++) {
            Rectangle2D.Double t = new Rectangle2D.Double(i, 0, 1, 1);
            String displayInterval = TimeFormatter.localTimeArrToDisplayFormat(intervals.get(i - 1));
            Cell cell = new Cell(t, BASE_COLOR, displayInterval, null);
            cell.setCenterFontSize(font, (float) (this.scale.x * 5F));

            cells.add(cell);
        }

        // courses
        for(var course : courses) {
            int startX = 0;
            int endX = 0;
            LocalTime[] courseIntArr = course.getIntervalAsLocalTimeArr();

            for(int i = 0; i < intervals.size(); i++) {
                if(intervals.get(i)[0].equals(courseIntArr[0])) startX = i + 1;
                if(intervals.get(i)[1].equals(courseIntArr[1])) endX = i + 2;
            }

            int dayIndex = course.getDayIndex();

            Cell clsDrw = new Cell(
                    new Rectangle2D.Double(startX, dayIndex * 2 + 1, endX - startX, 2),
                    BASE_COLOR,
                    course.getCourseName() + "\n(" + course.getHall() + ")",
                    course.getHuType());

            clsDrw.setSouthFontSize(font, (float) (this.scale.x * 5F));
            clsDrw.col = CLASS_COLORS[0];

            cells.add(clsDrw);
        }

        return cells;
    }

}
