package net.olemartin.domain;

public enum Color {
    BLACK, WHITE;

    public Color getOther() {
        return this == WHITE ? BLACK : WHITE;
    }
}
