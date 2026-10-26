package com.handloom.marketplace.entity;

public enum OrderStatus {
    PLACED("Placed", "bg-primary"),
    SHIPPED("Shipped", "bg-info text-dark"),
    DELIVERED("Delivered", "bg-success"),
    CANCELLED("Cancelled", "bg-secondary");

    private final String label;
    private final String badgeClass;

    OrderStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
