import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

record Person(String name, int floor) {
	public Person(String name) {
		this(name, 0);
	}

	public Person withFloor(int floor) {
		return new Person(this.name, floor);
	}

	public boolean isRightFloor(int floor) {
		return this.floor() > 0 && this.floor() == floor;
	}
}

enum Direction {
	UP,
	DOWN,
	NONE
}

class Elevator {
	public int currentFloor;
	private final BlockingQueue<Integer> floorsToVisit;

	public Elevator(int currentFloor) {
		this.currentFloor = currentFloor;
		this.floorsToVisit = new LinkedBlockingQueue<>();
	}

	public void enter(Person... people) {
		for (Person person : people) {
			if (person.isRightFloor(person.floor())) {
				floorsToVisit.add(person.floor());
			}
		}
	}

	public void leave(Person... people) {
		for (Person person : people) {
			System.out.println(person.name() + " left");
			floorsToVisit.remove(person.floor());
		}
	}

	public Direction nextDirection() {
		if (floorsToVisit.isEmpty()) {
			return Direction.NONE;
		}

		Integer floor = floorsToVisit.peek();
		if (floor > currentFloor) {
			return Direction.UP;
		} else {
			return Direction.DOWN;
		}
	}

	@SuppressWarnings("BusyWait")
	public static void main(String[] args) {
		int numFloors = 10;

		Elevator elevator = new Elevator(1);
		Person john = new Person("John", 5);
		Person bob = new Person("Bob", 3);
		elevator.enter(john, bob);

		System.out.println("Next direction: " + elevator.nextDirection());
		System.out.println("Floors to visit: " + elevator.floorsToVisit);

		for (int i = 0; i < numFloors; i++) {
			elevator.currentFloor = i + 1;
			System.out.println("Current floor: " + elevator.currentFloor);

			if (john.isRightFloor(elevator.currentFloor)) {
				elevator.leave(john);
			}

			if (bob.isRightFloor(elevator.currentFloor)) {
				elevator.leave(bob);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Interrupted, exiting");
				return;
			}
		}
	}
}
