enum CoffeeType {
	ESPRESSO,
	AMERICANO,
	CAPPUCCINO,
	LATTE
}

interface CoffeeMaker {
	void fill(CoffeeType[] mixture);
	void brew();
	String getModel();

	default void addWater() {
		System.out.println("Adding water");
	}

	default void addCoffee() {
		System.out.println("Adding coffee");
	}

	default void addMilk() {
		System.out.println("Adding milk");
	}
}

class PodCoffeeMaker implements CoffeeMaker {
	private CoffeeType[] mixture = new CoffeeType[3];

	@Override
	public void fill(CoffeeType[] mix) {
		mix[0] = CoffeeType.ESPRESSO;
		mix[1] = CoffeeType.AMERICANO;
		mix[2] = CoffeeType.CAPPUCCINO;
		this.mixture = mix;
		System.out.println("Adding coffee pods");
	}

	@Override
	public void brew() {
		StringBuilder mix = new StringBuilder();
		for (CoffeeType type : mixture) {
			mix.append(type).append(", ");
		}

		System.out.println("Brewing " + mix.toString() + "from pods");
	}

	@Override
	public String getModel() {
		return "Pod Coffee Maker";
	}

	public static void main(String[] args) {
		PodCoffeeMaker maker = new PodCoffeeMaker();
		maker.fill(maker.mixture);
		maker.brew();
	}
}