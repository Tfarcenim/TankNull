package tfar.tanknull;

public enum UseMode {
    bag(false),bucket_fill(true),bucket_empty(true);

    public final boolean interactive;

    UseMode(boolean interactive) {
        this.interactive = interactive;
    }

    public String translation() {
        return "tanknull.tank.use_mode."+name();
    }

}
