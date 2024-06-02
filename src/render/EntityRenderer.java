package render;

import entity.AbstractEntity;
import property.EntityType;
import until.Vector_Math;

import java.awt.*;

public class EntityRenderer extends Component implements Cloneable {

    public EntityRenderer(AbstractEntity entity) {
        this.entity = entity;
    }

    private AbstractEntity entity;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        Color color = g2D.getColor();

        int x = (int) entity.getPos().getX() + 960;
        int y = (int) entity.getPos().getY() + 540;

        Vector_Math arrowBaseLine = entity.getVelocity().clone().multi(10);
        Vector_Math arrowPart1 = entity.getVelocity().getRotateVector2D(-135 * Math.PI / 180).multi(10 / entity.getVelocity().length());
        Vector_Math arrowPart2 = entity.getVelocity().getRotateVector2D(135 * Math.PI / 180).multi(10 / entity.getVelocity().length());

        if (entity.getEntityType() == EntityType.CONSUMER) {
            g2D.setColor(Color.RED);
        }
        else {
            g2D.setColor(Color.GREEN);
        }
        g2D.fillOval(x, y, 10, 10);
        g2D.setColor(Color.BLACK);
        g2D.drawOval(x, y, 10, 10);
        g2D.setColor(Color.BLUE);
        g2D.drawLine(x + 5, y + 5, (int) arrowBaseLine.getX() + x + 5, (int) arrowBaseLine.getY() + y + 5);
        g2D.drawLine((int) arrowBaseLine.getX() + x + 5, (int) arrowBaseLine.getY() + y + 5, (int) ((int) arrowBaseLine.getX() + x + 5 + arrowPart1.getX()), (int) ((int) arrowBaseLine.getY() + y + 5 + arrowPart1.getY()));
        g2D.drawLine((int) arrowBaseLine.getX() + x + 5, (int) arrowBaseLine.getY() + y + 5, (int) ((int) arrowBaseLine.getX() + x + 5 + arrowPart2.getX()), (int) ((int) arrowBaseLine.getY() + y + 5 + arrowPart2.getY()));


        g2D.setColor(color);
    }

    @Override
    public EntityRenderer clone() {
        try {
            return (EntityRenderer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public EntityRenderer clone(AbstractEntity entity) {
        EntityRenderer clone = this.clone();
        clone.entity = entity;
        return clone;
    }
}
