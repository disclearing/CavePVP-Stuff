package net.valorhcf.knockback;

public class CraftKnockback implements Knockback {

    private String name;
    private double friction = 2.0D;
    private double horizontal = 0.35D;
    private double vertical = 0.35D;
    private double verticalLimit = 0.4000000059604645D;
    private double extraHorizontal = 0.425D;
    private double extraVertical = 0.085D;

    public CraftKnockback(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public double getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(double horizontal) {
        this.horizontal = horizontal;
    }

    public double getVertical() {
        return vertical;
    }

    public void setVertical(double vertical) {
        this.vertical = vertical;
    }

    public double getVerticalLimit() {
        return verticalLimit;
    }

    public void setVerticalLimit(double verticalLimit) {
        this.verticalLimit = verticalLimit;
    }

    public double getExtraHorizontal() {
        return extraHorizontal;
    }

    public void setExtraHorizontal(double extraHorizontal) {
        this.extraHorizontal = extraHorizontal;
    }

    public double getExtraVertical() {
        return extraVertical;
    }

    public void setExtraVertical(double extraVertical) {
        this.extraVertical = extraVertical;
    }

}
