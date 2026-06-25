import java.util.ArrayList;
import java.util.List;
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
	private final List<Integer> floors;
	public int currentFloor;
	private final BlockingQueue<Integer> floorsToVisit;

	public Elevator(List<Integer> floors, int currentFloor) {
		this.floors = floors;
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

		for (int floor : floors) {
			if (floorsToVisit.contains(floor)) {
				if (floor > currentFloor) {
					return Direction.UP;
				} else {
					return Direction.DOWN;
				}
			}
		}

		return Direction.NONE;
	}

	public static void main(String[] args) {
		int numFloors = 10;

		List<Integer> floors = new ArrayList<>();
		for (int i = 0; i < numFloors; i++) {
			floors.add(i + 1);
		}

		Elevator elevator = new Elevator(floors, 1);
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
		}
	}
}
