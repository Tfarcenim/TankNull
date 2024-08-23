package tfar.tanknull;

public enum TankStats {
    zero(0, 0),
    one(9, 4000),
    two(18, 16000),
    three(27, 64000),
    four(36, 256_000),
    five(45, 65536),
    six(54, 262144),
    seven(81, Integer.MAX_VALUE);

    public int slots;
    public int stacklimit;

    TankStats(int slots, int stacklimit) {
        this.slots = slots;
        this.stacklimit = stacklimit;
    }

    public void set(int slots, int stacklimit) {
        this.slots = slots;
        this.stacklimit = stacklimit;
    }
}
