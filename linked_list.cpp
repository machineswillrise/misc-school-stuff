#include <iostream>
#include <stdexcept>

template <typename T>
struct Node
{
	T data;
	Node<T> *next;
};

template <typename T>
class LinkedList
{
private:
	Node<T> *master_node;
	int length;

	void check_index(int index)
	{
		if (index < 0 || index >= length)
		{
			throw std::out_of_range("Index is out of bounds");
		}
	}

public:
	LinkedList(T elements[], int length) : length(length)
	{
		if (length == 0)
		{
			throw std::invalid_argument("Size must be positive");
		}

		Node<T> *head = new Node<T>();
		head->data = elements[0];
		head->next = nullptr;

		Node<T> *current = head;

		for (int i = 1; i < length; i++)
		{
			Node<T> *new_node = new Node<T>();
			new_node->data = elements[i];
			new_node->next = nullptr;

			current->next = new_node;
			current = new_node;
		}

		master_node = head;
	}

	void add(T data)
	{
		// look for the last node
		Node<T> *last_node = master_node;

		for (int i = 0; i < length - 1; i++)
		{
			last_node = last_node->next;
		}

		// make a new node
		Node<T> *new_node = new Node<T>();
		new_node->data = data;
		new_node->next = nullptr;

		// link the last node to it
		last_node->next = new_node;

		length++;
	}

	void remove(int index)
	{
		check_index(index);

		// special case: replace the head if the index is 0
		if (index == 0)
		{
			Node<T> *target = master_node;
			master_node = master_node->next;

			delete target;
			length--;

			return;
		}

		// find the node right in front of the target
		Node<T> *current = master_node;

		for (int i = 0; i < index - 1; i++)
		{
			current = current->next;
		}

		// replace the target node with the node in front of it and then delete it
		Node<T> *target = current->next;
		current->next = target->next;
		delete target;

		length--;
	}

	T get(int index)
	{
		check_index(index);

		Node<T> *node = master_node;

		for (int i = 0; i < index; i++)
		{
			node = node->next;
		}

		return node->data;
	}

	int size()
	{
		return length;
	}

	// destructor that deletes the list
	~LinkedList()
	{
		Node<T> *current = master_node;

		while (current != nullptr)
		{
			Node<T> *next = current->next;
			delete current;
			current = next;
		}
	}
};

int main()
{
	int numbers[] = { 1, 2, 3, 4, 5 };
	int length = sizeof(numbers) / sizeof(numbers[0]);
	LinkedList<int> list(numbers, length);

	std::cout << "Elements:" << "\n";
	for (int i = 0; i < 5; i++)
	{
		std::cout << list.get(i) << " ";
	}

	std::cout << "\n" << "Removing first element..." << "\n";
	list.remove(0);

	std::cout << "New first element: " << list.get(0) << "\n";
	std::cout << "New size: " << list.size() << "\n";

	std::cout << "When this goes out of scope, the destructor will run and clean it up." << "\n";

	return 0;
}