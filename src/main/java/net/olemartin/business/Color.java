package net.olemartin.business;

public enum Color {
    BLACK, WHITE;

    public Color getOther() {
        return this == WHITE ? BLACK : WHITE;
    }
}
