package tfar.tanknull;

public enum TankStats {
	zero(0,0),
	one(3, 4000),
	two(6, 16000),
	three(9,64000),
	four(12,256000),
	five(15,1024000),
	six(18,4096000),
	seven(27,Integer.MAX_VALUE);

	public int slots;
	public int capacity;

	TankStats(int slots, int capacity) {
		this.slots = slots;
		this.capacity = capacity;
	}

	public void set(int slots, int stacklimit) {
		this.slots = slots;
		this.capacity = stacklimit;
	}

}
